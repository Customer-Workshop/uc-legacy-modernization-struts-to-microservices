package com.northstar.workbench.controller;

import com.northstar.workbench.service.WorkbenchApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
public class AdminController {
  private final WorkbenchApplicationService service;

  public AdminController(WorkbenchApplicationService service) {
    this.service = service;
  }

  @PostMapping("/reset")
  public ResponseEntity<Void> reset() {
    service.reset();
    return ResponseEntity.noContent().build();
  }
}
