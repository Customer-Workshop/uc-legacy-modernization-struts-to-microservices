package com.northstar.settlement.repository;

import com.northstar.settlement.model.Settlement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettlementRepository extends JpaRepository<Settlement, Integer> {
  @Query("select coalesce(max(s.settlementId), 0) + 1 from Settlement s")
  int nextId();

  Settlement findFirstByClaimIdOrderBySettlementIdDesc(Integer claimId);

  @Query(
      "select s from Settlement s where s.settlementId ="
          + " (select max(s2.settlementId) from Settlement s2 where s2.claimId = s.claimId)")
  List<Settlement> findLatestPerClaim();
}
