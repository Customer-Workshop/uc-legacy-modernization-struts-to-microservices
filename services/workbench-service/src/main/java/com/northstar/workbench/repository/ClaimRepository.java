package com.northstar.workbench.repository;

import com.northstar.workbench.model.Claim;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Integer> {
  List<Claim> findByStatusOrderByClaimId(String status);
}
