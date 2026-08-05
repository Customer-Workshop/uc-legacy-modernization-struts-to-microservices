package com.northstar.policy.repository;

import com.northstar.policy.model.Policy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Integer> {
  List<Policy> findByLineOfBusinessOrderByPolicyNumber(String lineOfBusiness);
}
