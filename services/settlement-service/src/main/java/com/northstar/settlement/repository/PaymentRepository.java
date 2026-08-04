package com.northstar.settlement.repository;

import com.northstar.settlement.model.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
  @Query("select coalesce(max(p.paymentId), 0) + 1 from Payment p")
  int nextId();

  List<Payment> findByClaimIdOrderByPaymentIdAsc(Integer claimId);

  @Query("select coalesce(sum(p.amount), 0) from Payment p where p.claimId = :claimId")
  double totalIssued(Integer claimId);

  @Query("select p.claimId, count(p) from Payment p group by p.claimId")
  List<Object[]> countByClaim();
}
