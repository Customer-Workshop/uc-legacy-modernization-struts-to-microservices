package com.northstar.policy.controller;

import com.northstar.policy.dto.PolicyResponse;
import com.northstar.policy.service.PolicyApplicationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {
  private final PolicyApplicationService service;

  public PolicyController(PolicyApplicationService service) {
    this.service = service;
  }

  @GetMapping
  public List<PolicyResponse> search(@RequestParam(required = false) String lineOfBusiness) {
    // legacy-faithful: blank or missing line of business searches AUTO.
    String line = lineOfBusiness == null || lineOfBusiness.isEmpty() ? "AUTO" : lineOfBusiness;
    return service.findByLine(line).stream().map(PolicyResponse::from).toList();
  }

  @GetMapping("/{id}")
  public PolicyResponse view(@PathVariable String id) {
    int policyId;
    try {
      policyId = Integer.parseInt(id);
    } catch (RuntimeException ignored) {
      // legacy-faithful: PolicyViewAction falls back to policy 1 for an invalid identifier.
      policyId = 1;
    }
    return PolicyResponse.from(service.findById(policyId));
  }
}
