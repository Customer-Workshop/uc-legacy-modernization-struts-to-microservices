package com.northstar.workbench.repository;

import com.northstar.workbench.model.Claim;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClaimRepository extends JpaRepository<Claim, Integer> {
  @Modifying
  @Query("update Claim c set c.assignedAdjuster = :adjuster where c.claimId = :claimId")
  int updateAdjuster(@Param("claimId") int claimId, @Param("adjuster") String adjuster);

  @Modifying
  @Query("update Claim c set c.status = :status where c.claimId = :claimId")
  int updateStatus(@Param("claimId") int claimId, @Param("status") String status);

  @Modifying
  @Query("update Claim c set c.reserveAmount = :reserve where c.claimId = :claimId")
  int updateReserve(@Param("claimId") int claimId, @Param("reserve") BigDecimal reserve);
}
