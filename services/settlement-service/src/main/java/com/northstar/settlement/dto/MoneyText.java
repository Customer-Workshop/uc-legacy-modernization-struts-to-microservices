package com.northstar.settlement.dto;

import java.util.Locale;

/** Renders money exactly like the legacy JSP {@code FieldTag}: {@code %.2f} of a double. */
final class MoneyText {
  private MoneyText() {}

  static String of(double value) {
    return String.format(Locale.US, "%.2f", value);
  }
}
