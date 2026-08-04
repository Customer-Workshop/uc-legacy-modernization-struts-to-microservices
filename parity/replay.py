"""Replay golden Struts transcripts against extracted services."""
from __future__ import annotations

import argparse
import json
import os
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass
from pathlib import Path
from typing import cast

import yaml

ROOT = Path(__file__).parent.parent


@dataclass
class Result:
    scenario: str
    status: str
    message: str = ""


def request(base: str, method: str, path: str, body: dict[str, object] | None = None) -> tuple[int, object]:
    data = json.dumps(body).encode() if body is not None else None
    req = urllib.request.Request(base + path, data=data, method=method, headers={"Content-Type": "application/json"})
    try:
        with urllib.request.urlopen(req, timeout=10) as response:
            payload = response.read()
            return response.status, json.loads(payload) if payload else {}
    except urllib.error.HTTPError as error:
        return error.code, json.loads(error.read())


def parse_path(path: str) -> tuple[str, dict[str, str]]:
    parsed = urllib.parse.urlsplit(path)
    return parsed.path, dict(urllib.parse.parse_qsl(parsed.query))


def run(args: argparse.Namespace) -> list[Result]:
    spec = yaml.safe_load((ROOT / "parity/routes.yaml").read_text())
    index = json.loads((ROOT / "transcripts/index.json").read_text())
    selected = [entry for entry in index if (not args.module or entry["module"] == args.module)
                and (not args.scenario or entry["scenario"] == args.scenario)]
    bases = {"policy": args.base_url_policy.rstrip("/"), "intake": args.base_url_intake.rstrip("/")}
    for service in bases.values():
        try:
            request(service, "POST", "/internal/reset")
        except (urllib.error.URLError, TimeoutError):
            pass
    results: list[Result] = []
    for entry in selected:
        scenario = entry["scenario"]
        module = entry["module"]
        if spec["modules"].get(module) != "extracted":
            results.append(Result(scenario, "SKIP", "not yet extracted"))
            continue
        transcript = json.loads((ROOT / "transcripts" / f"{scenario}.json").read_text())
        route_path, query = parse_path(transcript["request"]["path"])
        route = spec["routes"][route_path]
        form = transcript["request"].get("form", {})
        target = route["url"]
        for key, value in query.items():
            target = target.replace("{" + key + "}", value)
        for key, value in form.items():
            target = target.replace("{" + key + "}", value)
        params = {target_key: query.get(source_key, form.get(source_key, "")) for target_key, source_key in route.get("query", {}).items()}
        if params:
            target += "?" + urllib.parse.urlencode(params)
        body = {target_key: form.get(source_key) for target_key, source_key in route.get("body", {}).items()}
        try:
            actual_status, actual = request(bases[route["service"]], route["method"], target, body or None)
        except (urllib.error.URLError, TimeoutError) as error:
            results.append(Result(scenario, "FAIL", f"request failed: {error}"))
            continue
        expected = transcript["expected"]
        expected_status = 2 if expected["validation_errors"] == [] else 4
        if actual_status // 100 != expected_status:
            results.append(Result(scenario, "FAIL", f"status {actual_status} != expected {expected_status}xx"))
            continue
        actual_data = cast(dict[str, object], actual) if isinstance(actual, dict) else {}
        errors = cast(list[str], actual_data.get("validationErrors", []))
        if errors != expected["validation_errors"]:
            results.append(Result(scenario, "FAIL", f"validationErrors {errors} != legacy {expected['validation_errors']}"))
            continue
        fields = extract_fields(scenario, actual, expected["business_fields"])
        mismatch = next((f"{key} {value} != legacy {expected['business_fields'][key]}"
                         for key, value in fields.items() if value != expected["business_fields"][key]), None)
        if mismatch:
            results.append(Result(scenario, "FAIL", mismatch))
            continue
        probe_error = check_probes(expected["db_state"], bases)
        results.append(Result(scenario, "FAIL", probe_error) if probe_error else Result(scenario, "PASS"))
    return results


def check_probes(expected: dict[str, str], bases: dict[str, str]) -> str:
    for probe, wanted in expected.items():
        parts = probe.split(".")
        if parts[0] == "policy":
            status, data = request(bases["policy"], "GET", f"/api/policies/{parts[1]}")
            actual = str(cast(dict[str, object], data).get({"limit": "policyLimit", "status": "status"}.get(parts[2], parts[2]), ""))
        elif parts[0] == "claim":
            status, data = request(bases["intake"], "GET", f"/api/claims/{parts[1]}")
            if parts[2] == "exists":
                actual = "true" if status == 200 else "false"
            else:
                actual = str(cast(dict[str, object], data).get({"loss_date": "lossDate"}.get(parts[2], parts[2]), ""))
        else:
            continue
        if actual != wanted:
            return f"{probe} {actual} != legacy {wanted}"
    return ""


def extract_fields(scenario: str, actual: object, expected: dict[str, str]) -> dict[str, str]:
    if scenario == "policy_search":
        items = cast(list[dict[str, str]], actual)
        return {f"policyLimit_{item['policyId']}": item["policyLimit"] for item in items}
    data = cast(dict[str, object], actual)
    if scenario == "policy_view":
        return {"policyLimit": str(data["policyLimit"]), "deductibleApplied": str(data["deductible"]), "policyStatus": str(data["status"])}
    if scenario in {"fnol_submit", "intake_lenient_date"}:
        return {"claimId": str(data["claimId"]), "claimStatus": str(data["claimStatus"])}
    return {key: str(data.get(key, "")) for key in expected}


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--base-url-policy", default=os.getenv("BASE_URL_POLICY", "http://localhost:8081"))
    parser.add_argument("--base-url-intake", default=os.getenv("BASE_URL_INTAKE", "http://localhost:8082"))
    parser.add_argument("--module")
    parser.add_argument("--scenario")
    args = parser.parse_args()
    results = run(args)
    for result in results:
        suffix = f" | {result.message}" if result.message else ""
        print(f"{result.scenario:32} {result.status}{suffix}")
    counts = {status: sum(result.status == status for result in results) for status in ("PASS", "FAIL", "SKIP")}
    summary = f"Summary: PASS={counts['PASS']} FAIL={counts['FAIL']} SKIP={counts['SKIP']}"
    print(summary)
    report = {"results": [result.__dict__ for result in results], "summary": counts}
    (ROOT / "parity/report.json").write_text(json.dumps(report, indent=2) + "\n")
    (ROOT / "parity/report.md").write_text("# Parity report\n\n```\n" + "\n".join(
        f"{result.scenario}: {result.status} {result.message}" for result in results) + f"\n```\n\n{summary}\n")
    return 1 if counts["FAIL"] else 0


if __name__ == "__main__":
    raise SystemExit(main())
