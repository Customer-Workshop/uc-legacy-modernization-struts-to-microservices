package com.northstar.settlement.service;

/** Request-parameter coercions carried over from the Struts ClaimsActionSupport helpers. */
public final class LegacyCoercions {

  private LegacyCoercions() {}

  /** legacy-faithful: unparseable integers fall back to a screen default (claimId -> 119). */
  public static int integer(String value, int fallback) {
    try {
      return Integer.parseInt(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }

  /** legacy-faithful: unparseable decimals fall back to a screen default (covered -> 5000). */
  public static double decimal(String value, double fallback) {
    try {
      return Double.parseDouble(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }
}
