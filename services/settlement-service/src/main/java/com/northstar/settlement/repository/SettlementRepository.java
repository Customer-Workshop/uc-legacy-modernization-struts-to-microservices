package com.northstar.settlement.repository;

import com.northstar.settlement.model.Settlement;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettlementRepository extends JpaRepository<Settlement, Integer> {
  @Query("select coalesce(max(s.settlementId), 0) + 1 from Settlement s")
  int nextId();

  Optional<Settlement> findTopByClaimIdOrderBySettlementIdDesc(Integer claimId);
}
