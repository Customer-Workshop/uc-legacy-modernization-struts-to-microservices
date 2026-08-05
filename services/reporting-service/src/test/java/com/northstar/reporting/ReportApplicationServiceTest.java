package com.northstar.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.northstar.reporting.dto.AgedClaimsRow;
import com.northstar.reporting.dto.LossRatioRow;
import com.northstar.reporting.dto.OpenByAdjusterRow;
import com.northstar.reporting.service.ReportApplicationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReportApplicationServiceTest {
  @Autowired ReportApplicationService service;

  @Test
  void openByAdjusterMatchesLegacyTranscript() {
    service.reset();
    List<OpenByAdjusterRow> rows = service.openByAdjuster();
    assertEquals(2, rows.size());
    assertEquals(new OpenByAdjusterRow("adjuster1", "24", "25404.00"), rows.get(0));
    assertEquals(new OpenByAdjusterRow("adjuster2", "25", "29928.00"), rows.get(1));
  }

  @Test
  void lossRatioMatchesLegacyTranscript() {
    service.reset();
    List<LossRatioRow> rows = service.lossRatio();
    assertEquals(4, rows.size());
    // quirk: premium totals include join fan-out (one premium contribution per claim row), so
    // AUTO shows 91400.00 rather than the sum of distinct AUTO policy premiums.
    assertEquals(new LossRatioRow("AUTO", "91400.00", "46270.00", "0.51"), rows.get(0));
    assertEquals(
        new LossRatioRow("COMMERCIAL_PROPERTY", "91000.00", "38211.00", "0.42"), rows.get(1));
    assertEquals(
        new LossRatioRow("GENERAL_LIABILITY", "93900.00", "34490.00", "0.37"), rows.get(2));
    assertEquals(new LossRatioRow("HOMEOWNERS", "93700.00", "37550.00", "0.40"), rows.get(3));
  }

  @Test
  void agedClaimsMatchesLegacyTranscript() {
    service.reset();
    List<AgedClaimsRow> rows = service.agedClaims();
    assertEquals(4, rows.size());
    // quirk: reserve totals on the aged screen render as truncated integers, not money.
    assertEquals(new AgedClaimsRow("0_30", "19", "23586"), rows.get(0));
    assertEquals(new AgedClaimsRow("31_60", "18", "19056"), rows.get(1));
    assertEquals(new AgedClaimsRow("61_90", "18", "19026"), rows.get(2));
    assertEquals(new AgedClaimsRow("91_PLUS", "18", "19116"), rows.get(3));
  }

  @Test
  void indexMirrorsReportIndexAction() {
    assertEquals("2019-04-01", service.index().reportAsOf());
    assertEquals(7, service.index().reportCount());
  }
}
