package com.northstar.settlement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.dto.SettlementRequest;
import com.northstar.settlement.model.Claim;
import com.northstar.settlement.model.Payment;
import com.northstar.settlement.model.Policy;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.ClaimRepository;
import com.northstar.settlement.repository.PaymentRepository;
import com.northstar.settlement.repository.PolicyRepository;
import com.northstar.settlement.repository.SettlementRepository;
import com.northstar.settlement.service.SettlementApplicationService;
import java.time.LocalDate;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SettlementApplicationServiceTest {
  private SettlementRepository settlements;
  private PaymentRepository payments;
  private PolicyRepository policies;
  private ClaimRepository claims;
  private SettlementApplicationService service;

  @BeforeEach
  void setUp() {
    settlements = mock(SettlementRepository.class);
    payments = mock(PaymentRepository.class);
    policies = mock(PolicyRepository.class);
    claims = mock(ClaimRepository.class);
    service =
        new SettlementApplicationService(
            settlements, payments, policies, claims, mock(DataSource.class));
    when(claims.findById(119)).thenReturn(Optional.of(new Claim(119, 9001)));
    when(policies.findById(9001)).thenReturn(Optional.of(new Policy(9001, 1000.0)));
    when(settlements.save(any(Settlement.class))).thenAnswer(call -> call.getArgument(0));
    when(payments.save(any(Payment.class))).thenAnswer(call -> call.getArgument(0));
  }

  @Test
  void blankClaimIdFallsBackToClaim119() {
    var result = service.calculate(new SettlementRequest("", "5000.00", "500.00", "0.00"));
    assertThat(result.settlement().getClaimId()).isEqualTo(119);
    assertThat(result.policyLimit()).isEqualTo(1000.0);
  }

  @Test
  void blankCoveredAmountDefaultsTo5000AndBlankDepreciationToZero() {
    var result = service.calculate(new SettlementRequest("119", "", "500.00", ""));
    assertThat(result.settlement().getCoveredAmount()).isEqualTo(5000.0);
    assertThat(result.settlement().getDepreciation()).isEqualTo(0.0);
  }

  @Test
  void missingClaimFallsBackToTheDefaultLimitOnCalculate() {
    when(claims.findById(42)).thenReturn(Optional.empty());
    var result = service.calculate(new SettlementRequest("42", "50000.00", "0", "0"));
    assertThat(result.policyLimit()).isEqualTo(10000.0);
    assertThat(result.settlement().getCappedAtLimit()).isTrue();
    assertThat(result.settlement().getSettlementAmount()).isEqualTo(10000.0);
  }

  @Test
  void saveStampsTheRecordedSessionUserAndFixedAuditDate() {
    when(settlements.nextId()).thenReturn(121);
    var saved = service.save(new SettlementRequest("119", "5000.00", "500.00", "0.00"));
    assertThat(saved.getSettlementId()).isEqualTo(121);
    assertThat(saved.getCalculatedBy()).isEqualTo("supervisor");
    assertThat(saved.getCalculatedDate()).isEqualTo(LocalDate.of(2019, 4, 1));
    assertThat(saved.getSettlementAmount()).isEqualTo(1000.0);
  }

  @Test
  void issueDerivesCheckNumberFixedDateAndIssuedStatus() {
    when(payments.nextId()).thenReturn(61);
    when(settlements.findFirstByClaimIdOrderBySettlementIdDesc(119))
        .thenReturn(new Settlement(121, 119, 5000.0, 500.0, 0.0, true, 1000.0, "supervisor", null));
    var payment = service.issue(new PaymentRequest("119", "Reserved Claimant", "1000.00", "CHECK"));
    assertThat(payment.getCheckNumber()).isEqualTo("CHK-61");
    assertThat(payment.getIssuedDate()).isEqualTo(LocalDate.of(2019, 4, 3));
    assertThat(payment.getStatus()).isEqualTo("ISSUED");
    assertThat(payment.getSettlementId()).isEqualTo(121);
  }

  @Test
  void blankPaymentAmountDefaultsToTheLatestSettlementAmount() {
    when(payments.nextId()).thenReturn(61);
    when(settlements.findFirstByClaimIdOrderBySettlementIdDesc(119))
        .thenReturn(new Settlement(121, 119, 5000.0, 500.0, 0.0, true, 1000.0, "supervisor", null));
    var payment = service.issue(new PaymentRequest("119", "Reserved Claimant", "", "CHECK"));
    assertThat(payment.getAmount()).isEqualTo(1000.0);
  }

  @Test
  void blankHistoryClaimIdFallsBackToClaim119() {
    when(payments.findByClaimIdOrderByPaymentId(119)).thenReturn(java.util.List.of());
    var history = service.history("");
    assertThat(history.claimId()).isEqualTo(119);
  }
}
