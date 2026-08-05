package com.northstar.reporting.service;

import com.northstar.reporting.dto.AgedClaimsRow;
import com.northstar.reporting.dto.LossRatioRow;
import com.northstar.reporting.dto.OpenClaimsRow;
import com.northstar.reporting.repository.ReportRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReportApplicationService {
  // legacy-faithful: the reporting calendar is pinned to the configured
  // report.asof.date (2019-04-01), not the current date.
  private final LocalDate asOfDate;
  private final ReportRepository repository;

  public ReportApplicationService(
      ReportRepository repository, @Value("${report.asof.date:2019-04-01}") String asOfDate) {
    this.repository = repository;
    this.asOfDate = LocalDate.parse(asOfDate);
  }

  public List<OpenClaimsRow> openClaimsByAdjuster() {
    return repository.openClaimsByAdjuster().stream()
        .map(
            row ->
                new OpenClaimsRow(
                    (String) row.get("assigned_adjuster"),
                    // legacy-faithful: counts were read as doubles and rendered through the
                    // integer field formatter, (long) Double.parseDouble(value).
                    toLong(row.get("open_count")),
                    toDouble(row.get("reserve_total"))))
        .toList();
  }

  public List<LossRatioRow> lossRatioByLine() {
    return repository.lossRatioByLine().stream()
        .map(
            row -> {
              double premium = toDouble(row.get("premium_total"));
              // legacy-faithful: loss is reserve plus paid, added in double arithmetic.
              double loss = toDouble(row.get("reserve_total")) + toDouble(row.get("paid_total"));
              return new LossRatioRow(
                  (String) row.get("line_of_business"),
                  premium,
                  loss,
                  // legacy-faithful: a zero premium yields ratio 0 instead of an error,
                  // and the ratio is computed with double division.
                  premium == 0 ? 0 : loss / premium);
            })
        .toList();
  }

  public List<AgedClaimsRow> agedClaims() {
    return repository.agedClaims(asOfDate).stream()
        .map(
            row ->
                new AgedClaimsRow(
                    ((String) row.get("age_bucket")).trim(),
                    toLong(row.get("claim_count")),
                    // legacy-faithful: the aged report rendered reserve totals through the
                    // integer field formatter, truncating any fractional cents.
                    toLong(row.get("reserve_total"))))
        .toList();
  }

  public void reset() {
    // Reporting endpoints are read-only in this extraction; reset is intentionally idempotent.
  }

  private static double toDouble(Object value) {
    return ((Number) value).doubleValue();
  }

  private static long toLong(Object value) {
    return (long) ((Number) value).doubleValue();
  }
}
