package com.northstar.settlement.service;

import java.util.Locale;

/**
 * Money formatting exactly as the legacy JSP {@code FieldTag type="money"} rendered it: {@code
 * String.format("%.2f", double)}. Java formats from the shortest decimal representation of the
 * double, so 1.005 renders as "1.01" even though its binary value is just below 1.005.
 */
public final class LegacyMoney {
  private LegacyMoney() {}

  public static String format(double value) {
    return String.format(Locale.US, "%.2f", value);
  }
}
