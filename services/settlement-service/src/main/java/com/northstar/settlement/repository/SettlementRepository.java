package com.northstar.settlement.repository;

import com.northstar.settlement.model.Settlement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettlementRepository extends JpaRepository<Settlement, Integer> {
  Optional<Settlement> findFirstByClaimIdOrderBySettlementIdDesc(Integer claimId);

  List<Settlement> findAllByOrderBySettlementIdAsc();

  @Query("select coalesce(max(s.settlementId),0)+1 from Settlement s")
  int nextId();
}
