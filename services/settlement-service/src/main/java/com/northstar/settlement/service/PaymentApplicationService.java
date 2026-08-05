package com.northstar.settlement.service;

import com.northstar.settlement.dto.PaymentRequest;
import com.northstar.settlement.exception.NotFoundException;
import com.northstar.settlement.model.Payment;
import com.northstar.settlement.model.Settlement;
import com.northstar.settlement.repository.PaymentRepository;
import com.northstar.settlement.repository.SettlementRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentApplicationService {
  private final PaymentRepository payments;
  private final SettlementRepository settlements;

  public PaymentApplicationService(PaymentRepository payments, SettlementRepository settlements) {
    this.payments = payments;
    this.settlements = settlements;
  }

  @Transactional
  public Payment issue(PaymentRequest request) {
    // legacy-faithful: PaymentIssueAction defaults claimId 119 and amount to the settlement.
    int claimId = LegacyCoercions.integer(request.claimId(), 119);
    Settlement settlement =
        settlements
            .findFirstByClaimIdOrderBySettlementIdDesc(claimId)
            .orElseThrow(() -> new NotFoundException("settlement.notFound"));
    Payment payment = new Payment();
    int paymentId = payments.nextId();
    payment.setPaymentId(paymentId);
    payment.setClaimId(claimId);
    payment.setSettlementId(settlement.getSettlementId());
    payment.setPayeeName(request.payeeName());
    payment.setAmount(LegacyCoercions.decimal(request.amount(), settlement.getSettlementAmount()));
    payment.setPaymentMethod(request.paymentMethod());
    payment.setCheckNumber("CHK-" + paymentId);
    // legacy-faithful: PaymentIssueAction stamps the fixed date 2019-04-03.
    payment.setIssuedDate("2019-04-03");
    payment.setStatus("ISSUED");
    return payments.save(payment);
  }

  public List<Payment> history(String claimId) {
    return payments.findByClaimIdOrderByPaymentIdAsc(LegacyCoercions.integer(claimId, 119));
  }

  public Payment get(int id) {
    return payments.findById(id).orElseThrow(() -> new NotFoundException("payment.notFound"));
  }

  public Map<String, Map<String, Long>> countsByClaim() {
    Map<String, Long> counts = new LinkedHashMap<>();
    for (Payment payment : payments.findAll()) {
      counts.merge(String.valueOf(payment.getClaimId()), 1L, Long::sum);
    }
    return Map.of("claim", counts);
  }
}
