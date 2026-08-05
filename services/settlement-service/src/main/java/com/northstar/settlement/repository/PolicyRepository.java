package com.northstar.settlement.repository;

import com.northstar.settlement.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Integer> {}
