import json
import unittest
from pathlib import Path

from parity.html_report import render


class HtmlReportTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        fixture = Path(__file__).parent / "fixtures" / "report.sample.json"
        cls.output = render(json.loads(fixture.read_text()))

    def test_fail_rows_come_before_skip_and_pass(self) -> None:
        fail = self.output.index("settlement_half_cent")
        skip = self.output.index("workbench_assign")
        passed = self.output.index("settlement_calculate")
        self.assertLess(fail, skip)
        self.assertLess(skip, passed)

    def test_pending_module_and_totals_are_visible(self) -> None:
        self.assertIn(">pending<", self.output)
        self.assertIn("<strong>8</strong><span>PASS</span>", self.output)
        self.assertIn("<strong>1</strong><span>FAIL</span>", self.output)
        self.assertIn("<strong>4</strong><span>SKIP</span>", self.output)
        self.assertIn("<strong>13</strong><span>TOTAL SCENARIOS</span>", self.output)

    def test_no_external_assets_and_all_modules_appear(self) -> None:
        self.assertNotRegex(self.output, r"https?://")
        for module in ("policy", "intake", "settlement", "workbench", "reporting"):
            self.assertIn(f">{module}<", self.output)


if __name__ == "__main__":
    unittest.main()
