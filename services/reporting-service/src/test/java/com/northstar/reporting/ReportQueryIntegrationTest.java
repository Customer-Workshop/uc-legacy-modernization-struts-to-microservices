package com.northstar.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import com.northstar.reporting.dto.AgedClaimsRow;
import com.northstar.reporting.dto.LossRatioRow;
import com.northstar.reporting.dto.OpenClaimsRow;
import com.northstar.reporting.service.ReportApplicationService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReportQueryIntegrationTest {
  @Autowired private ReportApplicationService service;

  @Test
  void openClaimsByAdjusterMatchesLegacyRecording() {
    List<OpenClaimsRow> rows = service.openClaimsByAdjuster();
    OpenClaimsRow adjuster1 = byKey(rows, "adjuster1");
    OpenClaimsRow adjuster2 = byKey(rows, "adjuster2");
    assertThat(adjuster1.openCount()).isEqualTo(24L);
    assertThat(adjuster1.reserveTotal()).isEqualTo(25404.0);
    assertThat(adjuster2.openCount()).isEqualTo(25L);
    assertThat(adjuster2.reserveTotal()).isEqualTo(29928.0);
  }

  @Test
  void lossRatioJoinFanOutMatchesLegacyRecording() {
    List<LossRatioRow> rows = service.lossRatioByLine();
    LossRatioRow auto =
        rows.stream().filter(r -> r.lineOfBusiness().equals("AUTO")).findFirst().orElseThrow();
    // The premium total reflects the legacy join fan-out (premium summed once per
    // claim/payment row), not the sum of distinct policy premiums.
    assertThat(auto.premiumTotal()).isEqualTo(91400.0);
    assertThat(auto.lossTotal()).isEqualTo(46270.0);
    assertThat(Math.round(auto.lossRatio() * 100.0) / 100.0).isEqualTo(0.51);
  }

  @Test
  void agedClaimsBucketsMatchLegacyRecording() {
    List<AgedClaimsRow> rows = service.agedClaims();
    assertThat(rows)
        .extracting(AgedClaimsRow::bucket, AgedClaimsRow::claimCount, AgedClaimsRow::reserveTotal)
        .containsExactly(
            org.assertj.core.groups.Tuple.tuple("0_30", 19L, 23586L),
            org.assertj.core.groups.Tuple.tuple("31_60", 18L, 19056L),
            org.assertj.core.groups.Tuple.tuple("61_90", 18L, 19026L),
            org.assertj.core.groups.Tuple.tuple("91_PLUS", 18L, 19116L));
  }

  private static OpenClaimsRow byKey(List<OpenClaimsRow> rows, String adjuster) {
    return rows.stream().filter(r -> r.adjuster().equals(adjuster)).findFirst().orElseThrow();
  }
}
