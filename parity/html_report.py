"""Render the standalone parity dashboard."""
from __future__ import annotations

import html
from pathlib import Path
from typing import cast


def _escape(value: object) -> str:
    return html.escape(str(value), quote=True)


def _status(result: dict[str, object]) -> str:
    return str(result.get("status", ""))


def render(report: dict[str, object]) -> str:
    modules = cast(dict[str, object], report.get("modules", {}))
    results = cast(list[dict[str, object]], report.get("results", []))
    summary = cast(dict[str, object], report.get("summary", {}))
    total = len(results)
    failures = int(summary.get("FAIL", 0))
    verdict = "PASS" if failures == 0 else "FAIL"
    verdict_class = "pass" if failures == 0 else "fail"

    module_rows = []
    for module, state in modules.items():
        module_results = [result for result in results if result.get("module") == module]
        counts = {
            status: sum(
                _status(result) == status
                if status != "SKIP"
                else _status(result).startswith("SKIP")
                for result in module_results
            )
            for status in ("PASS", "FAIL", "SKIP")
        }
        state_text = str(state)
        state_class = "extracted" if state_text == "extracted" else "pending"
        module_rows.append(
            "<tr>"
            f"<td>{_escape(module)}</td>"
            f'<td><span class="badge {state_class}">{_escape(state_text)}</span></td>'
            f"<td>{counts['PASS']}</td><td>{counts['FAIL']}</td><td>{counts['SKIP']}</td>"
            "</tr>"
        )

    ordered_results = sorted(
        enumerate(results),
        key=lambda indexed: (
            {"FAIL": 0, "SKIP": 1, "PASS": 2}.get(
                "SKIP" if _status(indexed[1]).startswith("SKIP") else _status(indexed[1]),
                3,
            ),
            indexed[0],
        ),
    )
    scenario_rows = []
    for _, result in ordered_results:
        status = _status(result)
        status_name = "SKIP" if status.startswith("SKIP") else status
        scenario_rows.append(
            "<tr>"
            f"<td>{_escape(result.get('scenario', ''))}</td>"
            f"<td>{_escape(result.get('module', ''))}</td>"
            f'<td><span class="badge {status_name.lower()}">{_escape(status)}</span></td>'
            f"<td>{_escape(result.get('message', ''))}</td>"
            "</tr>"
        )

    return f"""<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Parity Dashboard</title>
<style>
:root {{ color-scheme: light; font-family: system-ui, sans-serif; background: #f4f7fb; color: #172033; }}
body {{ max-width: 1180px; margin: 0 auto; padding: 32px; }}
header {{ background: #172033; color: white; border-radius: 14px; padding: 28px; margin-bottom: 24px; }}
h1 {{ margin: 0 0 8px; font-size: 30px; }} h2 {{ margin-top: 0; }}
.timestamp {{ color: #bac5d8; font-size: 14px; }}
.metrics {{ display: flex; flex-wrap: wrap; gap: 12px; margin-top: 24px; }}
.metric {{ background: #263653; border-radius: 10px; padding: 12px 18px; min-width: 90px; }}
.metric strong {{ display: block; font-size: 25px; }} .metric span {{ color: #bac5d8; font-size: 12px; }}
.verdict {{ display: inline-block; margin-top: 20px; padding: 10px 18px; border-radius: 999px; font-weight: 700; }}
.verdict.pass {{ background: #b8f2d0; color: #075b35; }} .verdict.fail {{ background: #ffd0d0; color: #8a1515; }}
section {{ background: white; border-radius: 14px; padding: 24px; margin-bottom: 24px; box-shadow: 0 2px 10px #17203312; }}
table {{ width: 100%; border-collapse: collapse; }} th, td {{ text-align: left; padding: 12px 10px; border-bottom: 1px solid #e5eaf2; }}
th {{ color: #53627a; font-size: 12px; text-transform: uppercase; letter-spacing: .05em; }}
.badge {{ display: inline-block; border-radius: 999px; padding: 4px 9px; font-size: 12px; font-weight: 700; }}
.badge.pass, .badge.extracted {{ background: #d7f7e4; color: #08703d; }}
.badge.fail {{ background: #ffe0e0; color: #a11a1a; }} .badge.skip, .badge.pending {{ background: #fff0c2; color: #805d00; }}
td:last-child {{ color: #53627a; }} @media (max-width: 700px) {{ body {{ padding: 16px; }} section {{ padding: 16px; overflow-x: auto; }} }}
</style>
</head>
<body>
<header>
<h1>Parity Dashboard</h1>
<div class="timestamp">Generated at {_escape(report.get('generated_at', ''))}</div>
<div class="metrics">
<div class="metric"><strong>{summary.get('PASS', 0)}</strong><span>PASS</span></div>
<div class="metric"><strong>{summary.get('FAIL', 0)}</strong><span>FAIL</span></div>
<div class="metric"><strong>{summary.get('SKIP', 0)}</strong><span>SKIP</span></div>
<div class="metric"><strong>{total}</strong><span>TOTAL SCENARIOS</span></div>
</div>
<div class="verdict {verdict_class}">Overall gate: {_escape(verdict)}</div>
</header>
<section><h2>Module rollup</h2>
<table><thead><tr><th>Module</th><th>Migration</th><th>PASS</th><th>FAIL</th><th>SKIP</th></tr></thead>
<tbody>{''.join(module_rows)}</tbody></table></section>
<section><h2>Scenarios</h2>
<table><thead><tr><th>Scenario</th><th>Module</th><th>Status</th><th>Message</th></tr></thead>
<tbody>{''.join(scenario_rows)}</tbody></table></section>
</body>
</html>
"""


def write(report: dict[str, object], path: str | Path) -> None:
    Path(path).write_text(render(report), encoding="utf-8")
