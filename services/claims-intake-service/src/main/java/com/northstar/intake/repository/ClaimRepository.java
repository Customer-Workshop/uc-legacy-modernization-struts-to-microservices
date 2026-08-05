package com.northstar.intake.repository;

import com.northstar.intake.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClaimRepository extends JpaRepository<Claim, Integer> {
  @Query("select coalesce(max(c.claimId), 0) + 1 from Claim c")
  int nextId();
}
