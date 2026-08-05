package com.northstar.reporting.service;

import com.northstar.reporting.dto.AgedClaimsRow;
import com.northstar.reporting.dto.LossRatioRow;
import com.northstar.reporting.dto.OpenByAdjusterRow;
import com.northstar.reporting.dto.ReportIndexResponse;
import com.northstar.reporting.repository.ReportRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportApplicationService {
  private final ReportRepository repository;
  private final DataSource dataSource;
  private final LocalDate asOfDate;

  public ReportApplicationService(
      ReportRepository repository,
      DataSource dataSource,
      @Value("${northstar.report.asof-date}") String asOfDate) {
    this.repository = repository;
    this.dataSource = dataSource;
    // legacy-faithful: reports run against the fixed report.asof.date, never the wall clock.
    this.asOfDate = LocalDate.parse(asOfDate);
  }

  public List<OpenByAdjusterRow> openByAdjuster() {
    return repository.openClaimsByAdjuster().stream()
        .map(
            row ->
                new OpenByAdjusterRow(
                    row.adjuster(),
                    LegacyReportFormat.integer(row.openCount()),
                    LegacyReportFormat.money(row.reserveTotal())))
        .toList();
  }

  public List<LossRatioRow> lossRatio() {
    return repository.lossRatioByLine().stream()
        .map(
            row ->
                new LossRatioRow(
                    row.lineOfBusiness(),
                    LegacyReportFormat.money(row.premiumTotal()),
                    LegacyReportFormat.money(row.lossTotal()),
                    // legacy-faithful: the ratio is rendered through the money formatter.
                    LegacyReportFormat.money(
                        LegacyReportFormat.ratio(row.lossTotal(), row.premiumTotal()))))
        .toList();
  }

  public List<AgedClaimsRow> agedClaims() {
    Map<String, double[]> buckets = new TreeMap<>();
    for (ReportRepository.ClaimAgeRow claim : repository.openClaimAges()) {
      long age = ChronoUnit.DAYS.between(claim.reportedDate(), asOfDate);
      String bucket = age <= 30 ? "0_30" : age <= 60 ? "31_60" : age <= 90 ? "61_90" : "91_PLUS";
      double[] totals = buckets.computeIfAbsent(bucket, key -> new double[2]);
      totals[0]++;
      totals[1] += claim.reserveAmount();
    }
    List<AgedClaimsRow> rows = new ArrayList<>();
    for (Map.Entry<String, double[]> entry : buckets.entrySet()) {
      rows.add(
          new AgedClaimsRow(
              entry.getKey(),
              LegacyReportFormat.integer(entry.getValue()[0]),
              // legacy-faithful: the aged screen renders reserve totals as truncated integers.
              LegacyReportFormat.integer(entry.getValue()[1])));
    }
    return rows;
  }

  public ReportIndexResponse index() {
    return new ReportIndexResponse(
        asOfDate.toString(), 7, "claims operations", "standard", "supervisor", "html");
  }

  @Transactional
  public void reset() {
    new ResourceDatabasePopulator(new ClassPathResource("db/reset-reporting.sql"))
        .execute(dataSource);
  }
}
