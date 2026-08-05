package com.northstar.intake.dto;

import java.util.List;

public record ErrorResponse(List<String> validationErrors) {}
