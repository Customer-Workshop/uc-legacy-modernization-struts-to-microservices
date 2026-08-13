"""Replay golden Struts transcripts against extracted services."""
from __future__ import annotations

import argparse
import json
import os
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass
from decimal import Decimal, ROUND_HALF_UP
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
    req = urllib.request.Request(
        base + path,
        data=data,
        method=method,
        headers={"Content-Type": "application/json"},
    )
    try:
        with urllib.request.urlopen(req, timeout=10) as response:
            payload = response.read()
            return response.status, json.loads(payload) if payload else {}
    except urllib.error.HTTPError as error:
        payload = error.read()
        return error.code, json.loads(payload) if payload else {}


def parse_path(path: str) -> tuple[str, dict[str, str]]:
    parsed = urllib.parse.urlsplit(path)
    return parsed.path, dict(urllib.parse.parse_qsl(parsed.query))


def pointer(value: object, path: str) -> object:
    current = value
    if path in ("", "/"):
        return current
    for component in path.lstrip("/").split("/"):
        component = component.replace("~1", "/").replace("~0", "~")
        if isinstance(current, list):
            current = current[int(component)]
        elif isinstance(current, dict):
            current = current[component]
        else:
            raise KeyError(path)
    return current


def normalize(value: object, kind: str) -> str:
    if kind == "money":
        return str(Decimal(str(value)).quantize(Decimal("0.01"), rounding=ROUND_HALF_UP))
    if kind == "integer":
        return str(int(str(value)))
    if kind == "text":
        return str(value)
    raise ValueError(f"unsupported normalization: {kind}")


def extract_fields(route: dict[str, object], actual: object) -> dict[str, str]:
    fields = cast(list[dict[str, object]], route.get("business_fields", []))
    extracted: dict[str, str] = {}
    for spec in fields:
        if "foreach" in spec:
            foreach = cast(dict[str, object], spec["foreach"])
            items = cast(list[object], pointer(actual, str(foreach["from"])))
            item_key = str(foreach["item_key"])
            field_template = str(foreach["field"])
            value_from = str(foreach["value_from"])
            kind = str(foreach["normalize"])
            for item in items:
                key_value = pointer(item, f"/{item_key}")
                field = field_template.replace("{" + item_key + "}", str(key_value))
                extracted[field] = normalize(pointer(item, value_from), kind)
            continue
        field = str(spec["field"])
        extracted[field] = normalize(pointer(actual, str(spec["from"])), str(spec["normalize"]))
    return extracted


def expected_status_class(route: dict[str, object], expected: dict[str, object]) -> int:
    status_map = cast(dict[str, int], route["status"])
    result = str(expected["result"]).split(":", 1)[0]
    if cast(list[object], expected["validation_errors"]):
        return status_map["validation"]
    return status_map[result]


def reset_services(spec: dict[str, object], bases: dict[str, str]) -> None:
    services = {str(route["service"]) for route in cast(dict[str, dict[str, object]], spec["routes"]).values()}
    for service in services:
        try:
            status, _ = request(bases[service], "POST", "/internal/reset")
        except (urllib.error.URLError, TimeoutError) as error:
            raise RuntimeError(f"reset failed for {service}: {error}") from error
        if status // 100 != 2:
            raise RuntimeError(f"reset failed for {service}: HTTP {status}")


def resolve_probe(probes: dict[str, dict[str, object]], probe: str) -> tuple[str, list[str]]:
    """Match the longest dotted probe name, leaving the identifier and field segments."""
    parts = probe.split(".")
    for size in range(len(parts) - 1, 0, -1):
        name = ".".join(parts[:size])
        if name in probes:
            return name, parts[size:]
    return "", []


def check_probes(
    spec: dict[str, object], expected: dict[str, str], bases: dict[str, str]
) -> str:
    probes = cast(dict[str, dict[str, object]], spec.get("probes", {}))
    for probe, wanted in expected.items():
        name, segments = resolve_probe(probes, probe)
        if not name:
            return f"probe {probe} has no declarative routes.yaml definition"
        definition = probes[name]
        if not segments:
            return f"probe {probe} is missing an identifier"
        field_name = ".".join(segments[1:]) or "value"
        fields = cast(dict[str, dict[str, object]], definition.get("fields", {}))
        if field_name not in fields:
            return f"probe {probe} has no declarative field mapping"
        field = fields[field_name]
        target = str(definition["url"]).replace("{id}", segments[0])
        try:
            status, data = request(bases[str(definition["service"])], "GET", target)
        except (urllib.error.URLError, TimeoutError) as error:
            return f"probe {probe} request failed: {error}"
        if field.get("exists"):
            actual = "true" if status == 200 else "false" if status == 404 else f"HTTP {status}"
        elif status // 100 != 2:
            return f"probe {probe} returned HTTP {status}"
        elif field.get("count"):
            try:
                items = cast(list[object], pointer(data, str(field.get("from", "/"))))
                actual = str(len(items))
            except (KeyError, IndexError, TypeError, ValueError) as error:
                return f"probe {probe} extraction failed: {error}"
        else:
            try:
                actual = normalize(pointer(data, str(field["from"])), str(field["normalize"]))
            except (KeyError, IndexError, TypeError, ValueError) as error:
                return f"probe {probe} extraction failed: {error}"
        if actual != wanted:
            return f"{probe} {actual} != legacy {wanted}"
    return ""


def run(args: argparse.Namespace) -> list[Result]:
    spec = cast(dict[str, object], yaml.safe_load((ROOT / "parity/routes.yaml").read_text()))
    index = cast(list[dict[str, str]], json.loads((ROOT / "transcripts/index.json").read_text()))
    selected = [
        entry
        for entry in index
        if (not args.module or entry["module"] == args.module)
        and (not args.scenario or entry["scenario"] == args.scenario)
    ]
    bases = {
        "policy": args.base_url_policy.rstrip("/"),
        "intake": args.base_url_intake.rstrip("/"),
        "settlement": args.base_url_settlement.rstrip("/"),
    }
    reset_services(spec, bases)
    modules = cast(dict[str, str], spec["modules"])
    routes = cast(dict[str, dict[str, object]], spec["routes"])
    results: list[Result] = []
    for entry in selected:
        scenario = entry["scenario"]
        module = entry["module"]
        if modules.get(module) != "extracted":
            results.append(Result(scenario, "SKIP (not yet extracted)"))
            continue
        transcript = cast(
            dict[str, object],
            json.loads((ROOT / "transcripts" / f"{scenario}.json").read_text()),
        )
        request_data = cast(dict[str, object], transcript["request"])
        route_path, query = parse_path(str(request_data["path"]))
        if route_path not in routes:
            results.append(
                Result(scenario, "FAIL", f"route {route_path} has no declarative routes.yaml definition")
            )
            continue
        route = routes[route_path]
        form = cast(dict[str, str], request_data.get("form", {}))
        target = str(route["url"])
        for key, value in {**query, **form}.items():
            target = target.replace("{" + key + "}", value)
        params = {
            str(target_key): query.get(str(source_key), form.get(str(source_key), ""))
            for target_key, source_key in cast(dict[str, str], route.get("query", {})).items()
        }
        if params:
            target += "?" + urllib.parse.urlencode(params)
        body = {
            str(target_key): form.get(str(source_key))
            for target_key, source_key in cast(dict[str, str], route.get("body", {})).items()
        }
        try:
            actual_status, actual = request(
                bases[str(route["service"])], str(route["method"]), target, body or None
            )
        except (urllib.error.URLError, TimeoutError) as error:
            results.append(Result(scenario, "FAIL", f"request failed: {error}"))
            continue
        expected = cast(dict[str, object], transcript["expected"])
        try:
            expected_class = expected_status_class(route, expected)
            if actual_status // 100 != expected_class:
                results.append(
                    Result(scenario, "FAIL", f"status {actual_status} != expected {expected_class}xx")
                )
                continue
            try:
                actual_errors = cast(list[str], pointer(actual, str(route["validation_errors"])))
            except (KeyError, IndexError, TypeError, ValueError):
                actual_errors = []
            expected_errors = cast(list[str], expected["validation_errors"])
            if actual_errors != expected_errors:
                results.append(
                    Result(
                        scenario,
                        "FAIL",
                        f"validation errors {actual_errors} != legacy {expected_errors}",
                    )
                )
                continue
            expected_fields = cast(dict[str, str], expected["business_fields"])
            if expected_fields:
                actual_fields = extract_fields(route, actual)
                mismatch = next(
                    (
                        f"{key} {actual_fields.get(key, '<missing>')} != legacy {value}"
                        for key, value in expected_fields.items()
                        if actual_fields.get(key) != value
                    ),
                    None,
                )
                if mismatch:
                    results.append(Result(scenario, "FAIL", mismatch))
                    continue
            probe_error = check_probes(spec, cast(dict[str, str], expected["db_state"]), bases)
        except (KeyError, IndexError, TypeError, ValueError) as error:
            probe_error = f"declarative extraction failed: {error}"
        results.append(Result(scenario, "FAIL", probe_error) if probe_error else Result(scenario, "PASS"))
    return results


def write_report(results: list[Result], summary: str) -> None:
    report = {
        "results": [result.__dict__ for result in results],
        "summary": {
            status: sum(
                result.status == status
                if status != "SKIP"
                else result.status.startswith("SKIP")
                for result in results
            )
            for status in ("PASS", "FAIL", "SKIP")
        },
    }
    (ROOT / "parity/report.json").write_text(json.dumps(report, indent=2) + "\n")
    rows = "\n".join(
        f"| {result.scenario} | {result.status} | {result.message} |" for result in results
    )
    (ROOT / "parity/report.md").write_text(
        f"# Parity report\n\n| Scenario | Status | Message |\n|---|---|---|\n{rows}\n\n{summary}\n"
    )


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--base-url-policy", default=os.getenv("BASE_URL_POLICY", "http://localhost:8081"))
    parser.add_argument("--base-url-intake", default=os.getenv("BASE_URL_INTAKE", "http://localhost:8082"))
    parser.add_argument(
        "--base-url-settlement", default=os.getenv("BASE_URL_SETTLEMENT", "http://localhost:8083")
    )
    parser.add_argument("--module")
    parser.add_argument("--scenario")
    args = parser.parse_args()
    try:
        results = run(args)
    except RuntimeError as error:
        print(f"Parity setup failed: {error}")
        return 1
    for result in results:
        suffix = f" | {result.message}" if result.message else ""
        print(f"{result.scenario:32} {result.status}{suffix}")
    counts = {
        status: sum(
            result.status == status
            if status != "SKIP"
            else result.status.startswith("SKIP")
            for result in results
        )
        for status in ("PASS", "FAIL", "SKIP")
    }
    summary = f"Summary: PASS={counts['PASS']} FAIL={counts['FAIL']} SKIP={counts['SKIP']}"
    print(summary)
    write_report(results, summary)
    return 1 if counts["FAIL"] else 0


if __name__ == "__main__":
    raise SystemExit(main())
