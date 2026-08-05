package com.northstar.settlement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.northstar.settlement.dto.PaymentIssueRequest;
import com.northstar.settlement.dto.SettlementCalculateRequest;
import com.northstar.settlement.dto.SettlementSaveRequest;
import com.northstar.settlement.exception.NotFoundException;
import com.northstar.settlement.model.Claim;
import com.northstar.settlement.model.Payment;
import com.northstar.settlement.model.Policy;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.ClaimRepository;
import com.northstar.settlement.repository.PaymentRepository;
import com.northstar.settlement.repository.PolicyRepository;
import com.northstar.settlement.repository.SettlementRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class SettlementApplicationServiceTest {
  @Test
  void integerAndDecimalFallbacksMatchLegacyActions() {
    SettlementApplicationService service =
        new SettlementApplicationService(
            mock(ClaimRepository.class),
            mock(PolicyRepository.class),
            mock(SettlementRepository.class),
            mock(PaymentRepository.class),
            mock(SettlementCalculator.class),
            mock(DataSource.class));
    assertThat(service.integer(null, 119)).isEqualTo(119);
    assertThat(service.integer("bad", 119)).isEqualTo(119);
    assertThat(service.decimal(null, 5000)).isEqualTo(5000);
    assertThat(service.decimal("bad", 0)).isZero();
  }

  @Test
  void paymentFallbackUsesLatestSettlementAndLegacyCheckFormat() {
    SettlementRepository settlements = mock(SettlementRepository.class);
    PaymentRepository payments = mock(PaymentRepository.class);
    Settlement latest = new Settlement(120, 119, 1500, 500, 100, false, 123.45, "adjuster1", null);
    when(settlements.findFirstByClaimIdOrderBySettlementIdDesc(119))
        .thenReturn(Optional.of(latest));
    when(payments.nextId()).thenReturn(61);
    when(payments.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
    SettlementApplicationService service =
        new SettlementApplicationService(
            mock(ClaimRepository.class),
            mock(PolicyRepository.class),
            settlements,
            payments,
            mock(SettlementCalculator.class),
            null);

    Payment issued =
        service.issue(new PaymentIssueRequest("119", "Claimant 119", "invalid", "WIRE"));

    assertThat(issued.getAmount()).isEqualTo(123.45);
    assertThat(issued.getCheckNumber()).isEqualTo("CHK-61");
    assertThat(issued.getPaymentMethod()).isEqualTo("WIRE");
  }

  @Test
  void calculateDefaultsMissingClaimOrPolicyLimitToTenThousand() {
    ClaimRepository claims = mock(ClaimRepository.class);
    when(claims.findById(119)).thenReturn(Optional.empty());
    SettlementApplicationService service =
        new SettlementApplicationService(
            claims,
            mock(PolicyRepository.class),
            mock(SettlementRepository.class),
            mock(PaymentRepository.class),
            new SettlementCalculator(),
            mock(DataSource.class));

    CalculatedSettlement result =
        service.calculate(new SettlementCalculateRequest("119", "20000", "0", "0"));

    assertThat(result.policyLimit()).isEqualTo(10000.0);
    assertThat(result.cappedAtLimit()).isTrue();
    assertThat(result.settlementAmount()).isEqualTo(10000.0);
  }

  @Test
  void saveDereferencesClaimAndPolicyWithoutLimitFallback() {
    ClaimRepository claims = mock(ClaimRepository.class);
    when(claims.findById(119)).thenReturn(Optional.empty());
    SettlementApplicationService service =
        new SettlementApplicationService(
            claims,
            mock(PolicyRepository.class),
            mock(SettlementRepository.class),
            mock(PaymentRepository.class),
            new SettlementCalculator(),
            mock(DataSource.class));

    assertThatThrownBy(() -> service.save(new SettlementSaveRequest("119", "1000", "0", "0", null)))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void saveDefaultsOperatorToSupervisorAndUsesFixedLegacyDate() {
    ClaimRepository claims = mock(ClaimRepository.class);
    PolicyRepository policies = mock(PolicyRepository.class);
    SettlementRepository settlements = mock(SettlementRepository.class);
    Claim claim = mock(Claim.class);
    when(claim.getPolicyId()).thenReturn(9001);
    Policy policy = mock(Policy.class);
    when(policy.getPolicyLimit()).thenReturn(new BigDecimal("10000"));
    when(claims.findById(119)).thenReturn(Optional.of(claim));
    when(policies.findById(9001)).thenReturn(Optional.of(policy));
    when(settlements.nextId()).thenReturn(121);
    when(settlements.save(any(Settlement.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    SettlementApplicationService service =
        new SettlementApplicationService(
            claims,
            policies,
            settlements,
            mock(PaymentRepository.class),
            new SettlementCalculator(),
            mock(DataSource.class));

    Settlement blankOperator =
        service.save(new SettlementSaveRequest("119", "1000", "0", "0", " "));

    assertThat(blankOperator.getCalculatedBy()).isEqualTo("supervisor");
    assertThat(blankOperator.getCalculatedDate()).isEqualTo(LocalDate.of(2019, 4, 1));
  }
}
