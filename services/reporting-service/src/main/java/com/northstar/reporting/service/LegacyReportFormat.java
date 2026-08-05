package com.northstar.reporting.service;

import java.util.Locale;

/**
 * Reproduces the legacy {@code FieldTag}/{@code ReportFormatter} rendering rules so the JSON
 * business fields match the recorded screens character for character.
 */
public final class LegacyReportFormat {
  private LegacyReportFormat() {}

  /** legacy-faithful: FieldTag type="money" renders double values with String.format("%.2f"). */
  public static String money(double value) {
    return String.format(Locale.ROOT, "%.2f", value);
  }

  /**
   * legacy-faithful: FieldTag type="integer" truncates doubles with a (long) cast, not rounding.
   */
  public static String integer(double value) {
    return String.valueOf((long) value);
  }

  /** legacy-faithful: ReportDAO returns 0 when premium is zero instead of failing the division. */
  public static double ratio(double loss, double premium) {
    return premium == 0 ? 0 : loss / premium;
  }
}
