package com.northstar.intake.controller;

import com.northstar.intake.dto.*;
import com.northstar.intake.service.ClaimApplicationService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {
  private final ClaimApplicationService service;

  public ClaimController(ClaimApplicationService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody FnolRequest request) {
    List<String> errors = service.validate(request);
    if (!errors.isEmpty()) return ResponseEntity.badRequest().body(new ErrorResponse(errors));
    var claim = service.create(request);
    return ResponseEntity.status(201)
        .body(
            new FnolResponse(
                claim.getClaimId().toString(), claim.getStatus(), claim.getLossDate().toString()));
  }

  @GetMapping("/{id}")
  public ClaimResponse get(@PathVariable int id) {
    return ClaimResponse.from(service.get(id));
  }
}
