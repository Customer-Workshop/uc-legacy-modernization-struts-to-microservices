package com.northstar.reporting.controller;

import com.northstar.reporting.dto.AgedClaimsRow;
import com.northstar.reporting.dto.LossRatioRow;
import com.northstar.reporting.dto.OpenClaimsRow;
import com.northstar.reporting.service.ReportApplicationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
  private final ReportApplicationService service;

  public ReportController(ReportApplicationService service) {
    this.service = service;
  }

  @GetMapping("/open-claims-by-adjuster")
  public List<OpenClaimsRow> openClaimsByAdjuster() {
    return service.openClaimsByAdjuster();
  }

  @GetMapping("/loss-ratio")
  public List<LossRatioRow> lossRatio() {
    return service.lossRatioByLine();
  }

  @GetMapping("/aged-claims")
  public List<AgedClaimsRow> agedClaims() {
    return service.agedClaims();
  }
}
