package com.northstar.reporting.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Aggregate queries carried over from the legacy ReportDAO. */
@Repository
public class ReportRepository {
  private final JdbcTemplate jdbc;

  public ReportRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<Map<String, Object>> openClaimsByAdjuster() {
    // legacy-faithful: only OPEN and INVESTIGATING claims count as "open".
    return jdbc.queryForList(
        "select assigned_adjuster, count(*) as open_count, "
            + "coalesce(sum(reserve_amount),0) as reserve_total "
            + "from claim where status in ('OPEN','INVESTIGATING') "
            + "group by assigned_adjuster order by assigned_adjuster");
  }

  public List<Map<String, Object>> lossRatioByLine() {
    // legacy-faithful: the double left join fans out rows, so annual_premium is summed
    // once per claim/payment row and reserve_amount once per payment row, exactly as
    // the legacy ReportDAO query did.
    return jdbc.queryForList(
        "select p.line_of_business, sum(p.annual_premium) premium_total,"
            + " coalesce(sum(c.reserve_amount),0) reserve_total,"
            + " coalesce(sum(pay.amount),0) paid_total"
            + " from policy p left join claim c on p.policy_id = c.policy_id"
            + " left join payment pay on c.claim_id = pay.claim_id"
            + " group by p.line_of_business order by p.line_of_business");
  }

  public List<Map<String, Object>> agedClaims(LocalDate asOf) {
    // legacy-faithful: buckets compare the reported date against the fixed as-of
    // date with the same <=30/<=60/<=90 day boundaries, excluding CLOSED and DENIED,
    // ordered by the bucket label text.
    java.sql.Date d30 = java.sql.Date.valueOf(asOf.minusDays(30));
    java.sql.Date d60 = java.sql.Date.valueOf(asOf.minusDays(60));
    java.sql.Date d90 = java.sql.Date.valueOf(asOf.minusDays(90));
    return jdbc.queryForList(
        "select age_bucket, count(*) as claim_count, "
            + "coalesce(sum(reserve_amount),0) as reserve_total from ("
            + "select case when reported_date >= ? then '0_30' "
            + "when reported_date >= ? then '31_60' "
            + "when reported_date >= ? then '61_90' else '91_PLUS' end as age_bucket, "
            + "reserve_amount from claim where status not in ('CLOSED','DENIED')"
            + ") aged group by age_bucket order by age_bucket",
        d30,
        d60,
        d90);
  }
}
