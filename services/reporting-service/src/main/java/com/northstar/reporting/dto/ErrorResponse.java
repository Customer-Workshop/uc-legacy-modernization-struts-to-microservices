package com.northstar.reporting.dto;

import java.util.List;

public record ErrorResponse(List<String> validationErrors) {}
