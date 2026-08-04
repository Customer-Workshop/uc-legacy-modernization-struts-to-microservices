package com.northstar.policy.service;

import com.northstar.policy.exception.NotFoundException;
import com.northstar.policy.model.Policy;
import com.northstar.policy.repository.PolicyRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PolicyApplicationService {
  private final PolicyRepository repository;

  public PolicyApplicationService(PolicyRepository repository) {
    this.repository = repository;
  }

  public List<Policy> findByLine(String line) {
    return repository.findByLineOfBusinessOrderByPolicyNumber(line);
  }

  public Policy findById(int id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("policy.notFound"));
  }

  @Transactional
  public void reset() {
    // Policy endpoints are read-only in this extraction; reset is intentionally idempotent.
  }
}
