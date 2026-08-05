package com.northstar.workbench.dto;

import java.util.List;

public record ErrorResponse(List<String> validationErrors) {}
