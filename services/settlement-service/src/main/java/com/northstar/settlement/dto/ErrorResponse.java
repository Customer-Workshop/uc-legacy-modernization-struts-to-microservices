package com.northstar.settlement.dto;

import java.util.List;

public record ErrorResponse(List<String> validationErrors) {}
