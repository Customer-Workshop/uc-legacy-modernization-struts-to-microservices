package com.northstar.settlement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.northstar.settlement.service.SettlementApplicationService;
import org.junit.jupiter.api.Test;

/** Locks in the ClaimsActionSupport request-coercion fallbacks carried into the service. */
class LegacyCoercionTest {

  private final SettlementApplicationService service =
      new SettlementApplicationService(null, null, null, null, null, null);

  @Test
  void blankOrGarbledClaimIdFallsBackToLegacyDefault() {
    assertEquals(119, service.integer(null, 119));
    assertEquals(119, service.integer("", 119));
    assertEquals(119, service.integer("abc", 119));
    assertEquals(120, service.integer("120", 119));
  }

  @Test
  void blankOrGarbledAmountFallsBackToLegacyDefault() {
    assertEquals(5000.0, service.decimal(null, 5000));
    assertEquals(5000.0, service.decimal("not-a-number", 5000));
    assertEquals(1.005, service.decimal("1.005", 5000));
  }
}
