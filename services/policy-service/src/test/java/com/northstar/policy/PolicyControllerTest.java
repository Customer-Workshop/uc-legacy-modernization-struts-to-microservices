package com.northstar.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.northstar.policy.controller.PolicyController;
import com.northstar.policy.model.Policy;
import com.northstar.policy.service.PolicyApplicationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class PolicyControllerTest {
  private static Policy policy(String limit) {
    Policy policy = mock(Policy.class);
    when(policy.getPolicyId()).thenReturn(1);
    when(policy.getPolicyNumber()).thenReturn("POL-0001");
    when(policy.getLineOfBusiness()).thenReturn("AUTO");
    when(policy.getInsuredName()).thenReturn("Insured 1");
    when(policy.getInsuredAddress()).thenReturn("1 Main St");
    when(policy.getEffectiveDate()).thenReturn(LocalDate.of(2018, 1, 1));
    when(policy.getExpiryDate()).thenReturn(LocalDate.of(2019, 1, 1));
    when(policy.getPolicyLimit()).thenReturn(new BigDecimal(limit));
    when(policy.getDeductible()).thenReturn(new BigDecimal("500"));
    when(policy.getAnnualPremium()).thenReturn(new BigDecimal("1200.5"));
    when(policy.getStatus()).thenReturn("ACTIVE");
    return policy;
  }

  @Test
  void emptyPolicyLineDefaultsToAuto() {
    PolicyApplicationService service = mock(PolicyApplicationService.class);
    when(service.findByLine("AUTO")).thenReturn(List.of());
    PolicyController controller = new PolicyController(service);

    controller.search("");
    controller.search(null);

    verify(service, times(2)).findByLine("AUTO");
  }

  @Test
  void invalidPolicyIdFallsBackToPolicyOne() {
    Policy fallback = policy("25000");
    PolicyApplicationService service = mock(PolicyApplicationService.class);
    when(service.findById(1)).thenReturn(fallback);
    PolicyController controller = new PolicyController(service);

    assertThat(controller.view("not-a-number").policyId()).isEqualTo(1);
    verify(service).findById(1);
  }

  @Test
  void moneyFieldsRenderWithTwoDecimalPlaces() {
    Policy priced = policy("25000");
    PolicyApplicationService service = mock(PolicyApplicationService.class);
    when(service.findById(1)).thenReturn(priced);

    var response = new PolicyController(service).view("1");

    assertThat(response.policyLimit()).isEqualTo("25000.00");
    assertThat(response.deductible()).isEqualTo("500.00");
    assertThat(response.annualPremium()).isEqualTo("1200.50");
  }
}
