package com.northstar.reporting.dto;

public record ReportIndexResponse(
    String reportAsOf,
    int reportCount,
    String reportArea,
    String reportNavigation,
    String reportAccess,
    String reportFormat) {}
