package com.northstar.reporting.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepository {
  private final JdbcTemplate jdbc;

  public ReportRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public record AdjusterRow(String adjuster, double openCount, double reserveTotal) {}

  public record LossRatioRow(String lineOfBusiness, double premiumTotal, double lossTotal) {}

  public record ClaimAgeRow(LocalDate reportedDate, double reserveAmount) {}

  public List<AdjusterRow> openClaimsByAdjuster() {
    return jdbc.query(
        "select assigned_adjuster, count(*) as open_count,"
            + " coalesce(sum(reserve_amount),0) as reserve_total"
            + " from claim where status in ('OPEN','INVESTIGATING')"
            + " group by assigned_adjuster order by assigned_adjuster",
        (rs, i) ->
            new AdjusterRow(
                rs.getString("assigned_adjuster"),
                // legacy-faithful: ReportDAO reads counts and totals as doubles.
                rs.getDouble("open_count"),
                rs.getDouble("reserve_total")));
  }

  public List<LossRatioRow> lossRatioByLine() {
    // legacy-faithful: premium is summed per joined claim/payment row (join fan-out), so lines
    // with multiple claims per policy count that policy's premium once per row, as the legacy
    // report always has.
    return jdbc.query(
        "select p.line_of_business, sum(p.annual_premium) as premium_total,"
            + " coalesce(sum(c.reserve_amount),0) as reserve_total,"
            + " coalesce(sum(pay.amount),0) as paid_total"
            + " from policy p left join claim c on p.policy_id = c.policy_id"
            + " left join payment pay on c.claim_id = pay.claim_id"
            + " group by p.line_of_business order by p.line_of_business",
        (rs, i) ->
            new LossRatioRow(
                rs.getString("line_of_business"),
                rs.getDouble("premium_total"),
                // legacy-faithful: loss is reserve plus paid, accumulated in double arithmetic.
                rs.getDouble("reserve_total") + rs.getDouble("paid_total")));
  }

  public List<ClaimAgeRow> openClaimAges() {
    return jdbc.query(
        "select reported_date, reserve_amount from claim"
            + " where status not in ('CLOSED','DENIED')",
        (rs, i) ->
            new ClaimAgeRow(
                rs.getDate("reported_date").toLocalDate(), rs.getDouble("reserve_amount")));
  }
}
