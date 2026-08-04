package com.northstar.settlement;

import static org.assertj.core.api.Assertions.assertThat;

import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.PaymentRepository;
import com.northstar.settlement.repository.SettlementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SettlementSeedBootTest {
  @Autowired private SettlementRepository settlements;
  @Autowired private PaymentRepository payments;

  @Test
  void flywaySeedPreservesLegacyIdsAndClaim119State() {
    assertThat(settlements.nextId()).isEqualTo(121);
    assertThat(payments.nextId()).isEqualTo(61);

    Settlement latest = settlements.findFirstByClaimIdOrderBySettlementIdDesc(119).orElseThrow();
    assertThat(latest.getSettlementId()).isEqualTo(119);
    assertThat(latest.getSettlementAmount()).isZero();
    assertThat(payments.findByClaimIdOrderByPaymentIdAsc(119)).isEmpty();
  }
}
