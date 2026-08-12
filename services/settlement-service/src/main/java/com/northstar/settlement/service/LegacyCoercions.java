package com.northstar.settlement.service;

/**
 * The request-parameter coercions the Struts layer performed implicitly ({@code
 * ClaimsActionSupport.integer}/{@code decimal}): conversion failures fall back to a hardcoded
 * default instead of erroring.
 */
public final class LegacyCoercions {
  private LegacyCoercions() {}

  public static int integer(String value, int fallback) {
    try {
      return Integer.parseInt(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }

  public static double decimal(String value, double fallback) {
    try {
      return Double.parseDouble(value);
    } catch (RuntimeException failure) {
      return fallback;
    }
  }
}
