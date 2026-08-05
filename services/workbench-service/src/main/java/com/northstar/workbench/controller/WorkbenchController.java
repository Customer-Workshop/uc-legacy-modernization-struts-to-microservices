package com.northstar.workbench.controller;

import com.northstar.workbench.dto.WorkbenchDtos.*;
import com.northstar.workbench.exception.NotFoundException;
import com.northstar.workbench.service.WorkbenchApplicationService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workbench/claims")
public class WorkbenchController {
  private final WorkbenchApplicationService service;

  public WorkbenchController(WorkbenchApplicationService service) {
    this.service = service;
  }

  @GetMapping
  public WorkbenchListResponse list() {
    var open = service.openClaims().stream().map(ClaimSummaryResponse::from).toList();
    return new WorkbenchListResponse(open.size(), open);
  }

  @GetMapping("/{claimId}")
  public ClaimSummaryResponse view(@PathVariable String claimId) {
    return service
        .find(service.claimId(claimId))
        .map(ClaimSummaryResponse::from)
        .orElseThrow(() -> new NotFoundException("claim.notFound"));
  }

  @PostMapping("/{claimId}/assign")
  public AssignResponse assign(@PathVariable String claimId, @RequestBody AssignRequest request) {
    int id = service.claimId(claimId);
    String adjuster = service.assign(id, request.adjuster());
    return new AssignResponse(Integer.toString(id), adjuster);
  }

  @PostMapping("/{claimId}/status")
  public StatusResponse status(
      @PathVariable String claimId, @RequestParam(required = false) String status) {
    int id = service.claimId(claimId);
    String effective = service.changeStatus(id, status);
    return new StatusResponse(Integer.toString(id), effective);
  }

  @PostMapping("/{claimId}/reserve")
  public ReserveResponse reserve(
      @PathVariable String claimId, @RequestBody ReserveRequest request) {
    int id = service.claimId(claimId);
    double amount = service.changeReserve(id, request.reserveAmount());
    return new ReserveResponse(
        Integer.toString(id), String.format(java.util.Locale.ROOT, "%.2f", amount));
  }

  @PostMapping("/{claimId}/note")
  public NoteResponse note(@PathVariable String claimId, @RequestBody NoteRequest request) {
    int id = service.claimId(claimId);
    return new NoteResponse(Integer.toString(id), service.noteText(request.noteText()));
  }

  @GetMapping("/{claimId}/notes")
  public NoteHistoryResponse notes(@PathVariable String claimId) {
    int id = service.claimId(claimId);
    var history = service.noteHistory(id).stream().map(ClaimNoteResponse::from).toList();
    return new NoteHistoryResponse(Integer.toString(id), history);
  }

  @GetMapping("/{claimId}/status-history")
  public HistoryResponse statusHistory(@PathVariable String claimId) {
    // legacy-faithful: WorkbenchStatusHistoryAction always renders an empty list.
    return new HistoryResponse(Integer.toString(service.claimId(claimId)), List.of());
  }

  @GetMapping("/{claimId}/reserve-history")
  public HistoryResponse reserveHistory(@PathVariable String claimId) {
    // legacy-faithful: WorkbenchReserveHistoryAction always renders an empty list.
    return new HistoryResponse(Integer.toString(service.claimId(claimId)), List.of());
  }
}
