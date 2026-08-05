package com.northstar.reporting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.northstar.reporting.service.LegacyReportFormat;
import org.junit.jupiter.api.Test;

class LegacyReportFormatTest {

  @Test
  void moneyRendersTwoDecimalsLikeFieldTag() {
    assertEquals("29928.00", LegacyReportFormat.money(29928.0));
    assertEquals("0.51", LegacyReportFormat.money(0.50623632385));
  }

  @Test
  void integerTruncatesInsteadOfRounding() {
    // quirk: FieldTag type="integer" casts to long, so 23586.99 renders as 23586, never 23587.
    assertEquals("23586", LegacyReportFormat.integer(23586.99));
    assertEquals("19", LegacyReportFormat.integer(19.0));
  }

  @Test
  void ratioIsZeroWhenPremiumIsZero() {
    // quirk: legacy ReportDAO returns 0 instead of dividing by a zero premium.
    assertEquals(0.0, LegacyReportFormat.ratio(1234.0, 0.0));
  }

  @Test
  void ratioUsesDoubleDivisionRenderedAsMoney() {
    // quirk: the loss ratio is a plain double division rendered through the money formatter.
    assertEquals("0.51", LegacyReportFormat.money(LegacyReportFormat.ratio(46270.0, 91400.0)));
    assertEquals("0.37", LegacyReportFormat.money(LegacyReportFormat.ratio(34490.0, 93900.0)));
  }
}
