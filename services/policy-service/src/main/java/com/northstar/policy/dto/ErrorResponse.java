package com.northstar.policy.dto;

import java.util.List;

public record ErrorResponse(List<String> validationErrors) {}
