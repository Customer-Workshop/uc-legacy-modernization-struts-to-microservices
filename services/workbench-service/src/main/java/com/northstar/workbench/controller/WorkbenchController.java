package com.northstar.workbench.controller;

import com.northstar.workbench.dto.AssignRequest;
import com.northstar.workbench.dto.AssignResponse;
import com.northstar.workbench.dto.ClaimResponse;
import com.northstar.workbench.dto.ReserveRequest;
import com.northstar.workbench.dto.ReserveResponse;
import com.northstar.workbench.dto.StatusResponse;
import com.northstar.workbench.model.Claim;
import com.northstar.workbench.service.WorkbenchApplicationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workbench/claims")
public class WorkbenchController {
  private final WorkbenchApplicationService service;

  public WorkbenchController(WorkbenchApplicationService service) {
    this.service = service;
  }

  @PostMapping("/{claimId}/assign")
  public AssignResponse assign(
      @PathVariable String claimId, @RequestBody(required = false) AssignRequest request) {
    String adjuster = request == null ? null : request.adjuster();
    Claim claim = service.assign(claimId, adjuster);
    ClaimResponse view = ClaimResponse.from(claim);
    // legacy-faithful: the JSP renders f_assignedAdjuster from the request attribute (the
    // submitted/defaulted value), not the claim row, even when no claim was updated.
    return new AssignResponse(
        claimId, view == null ? adjusterOrDefault(adjuster) : view.assignedAdjuster(), view);
  }

  @PostMapping("/{claimId}/status")
  public StatusResponse status(
      @PathVariable String claimId, @RequestParam(required = false) String status) {
    Claim claim = service.changeStatus(claimId, status);
    ClaimResponse view = ClaimResponse.from(claim);
    // legacy-faithful: f_claimStatus echoes the request attribute regardless of DB effect.
    return new StatusResponse(
        claimId, view == null ? statusOrDefault(status) : view.status(), view);
  }

  @PostMapping("/{claimId}/reserve")
  public ReserveResponse reserve(
      @PathVariable String claimId, @RequestBody(required = false) ReserveRequest request) {
    double reserve =
        service.changeReserve(claimId, request == null ? null : request.reserveAmount());
    Claim claim = service.find(claimId);
    // legacy-faithful: f_reserveAmount echoes the parsed request attribute, not the stored row.
    return new ReserveResponse(
        claimId,
        java.math.BigDecimal.valueOf(reserve)
            .setScale(2, java.math.RoundingMode.HALF_UP)
            .toPlainString(),
        ClaimResponse.from(claim));
  }

  @GetMapping("/{id}")
  public ClaimResponse get(@PathVariable int id) {
    return ClaimResponse.from(service.get(id));
  }

  private String adjusterOrDefault(String adjuster) {
    return adjuster == null || adjuster.isEmpty()
        ? WorkbenchApplicationService.DEFAULT_ADJUSTER
        : adjuster;
  }

  private String statusOrDefault(String status) {
    return status == null || status.isEmpty() ? WorkbenchApplicationService.DEFAULT_STATUS : status;
  }
}
