package com.northstar.settlement.repository;

import com.northstar.settlement.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Integer> {}
