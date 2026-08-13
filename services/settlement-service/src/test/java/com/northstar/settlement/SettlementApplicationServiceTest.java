package com.northstar.settlement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.model.Payment;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.PaymentRepository;
import com.northstar.settlement.repository.SettlementRepository;
import com.northstar.settlement.service.SettlementApplicationService;
import com.northstar.settlement.service.SettlementCalculator;
import java.time.LocalDate;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class SettlementApplicationServiceTest {
  private final SettlementApplicationService service =
      new SettlementApplicationService(
          mock(SettlementRepository.class),
          mock(PaymentRepository.class),
          new SettlementCalculator(),
          mock(DataSource.class),
          "http://localhost:8082",
          "http://localhost:8081");

  @Test
  void integerFallbackMatchesLegacyCoercion() {
    assertEquals(119, service.integer("", 119));
    assertEquals(119, service.integer("bad", 119));
    assertEquals(42, service.integer("42", 119));
  }

  @Test
  void decimalFallbackMatchesLegacyCoercion() {
    assertEquals(5000, service.decimal("", 5000));
    assertEquals(5000, service.decimal("bad", 5000));
    assertEquals(1.005, service.decimal("1.005", 5000));
  }

  @Test
  void issueAllocatesLegacyCheckNumberAndPaymentMetadata() {
    SettlementRepository settlements = mock(SettlementRepository.class);
    PaymentRepository payments = mock(PaymentRepository.class);
    SettlementApplicationService application =
        new SettlementApplicationService(
            settlements,
            payments,
            new SettlementCalculator(),
            mock(DataSource.class),
            "http://localhost:8082",
            "http://localhost:8081");
    Settlement seeded =
        new Settlement(120, 119, 5000, 500, 0, 4500, false, "supervisor", LocalDate.of(2019, 4, 1));
    when(settlements.findFirstByClaimIdOrderBySettlementIdDesc(119))
        .thenReturn(Optional.of(seeded));
    when(payments.nextId()).thenReturn(61);
    when(payments.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Payment result = application.issue(new PaymentRequest("119", "", "Payee", "CHECK"));

    assertEquals(61, result.getPaymentId());
    assertEquals(119, result.getClaimId());
    assertEquals(120, result.getSettlementId());
    assertEquals("CHK-61", result.getCheckNumber());
    assertEquals("ISSUED", result.getStatus());
    assertEquals(LocalDate.of(2019, 4, 3), result.getIssuedDate());
    verify(payments).save(any(Payment.class));
  }
}
