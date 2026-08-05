package com.northstar.intake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.northstar.intake.dto.FnolRequest;
import com.northstar.intake.repository.ClaimRepository;
import com.northstar.intake.service.ClaimApplicationService;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class ClaimApplicationServiceTest {
  @Test
  void preservesOrderedValidationAndOptionalDate() {
    var service = new ClaimApplicationService(mock(ClaimRepository.class), mock(DataSource.class));
    assertThat(service.validate(new FnolRequest("", "", "2019-02-30", "FIRE")))
        .containsExactly(
            "errors.claimant.required", "errors.description.required", "errors.lossdate.format");
    assertThat(service.validate(new FnolRequest("A", "D", null, "FIRE"))).isEmpty();
  }

  @Test
  void reserveRendersTwoDecimalsWithHalfUpRounding() {
    var claim =
        new com.northstar.intake.model.Claim(
            121,
            "CLM-00121",
            9001,
            "Claimant 121",
            LocalDate.of(2019, 4, 1),
            LocalDate.of(2019, 4, 1),
            "WATER",
            "desc",
            "OPEN",
            new java.math.BigDecimal("1234.5670"),
            "adjuster1",
            "supervisor",
            LocalDate.of(2019, 4, 1));
    assertThat(com.northstar.intake.dto.ClaimResponse.from(claim).reserveAmount())
        .isEqualTo("1234.57");
  }

  @Test
  void lenientDateRollsFebruaryForward() {
    var service = new ClaimApplicationService(mock(ClaimRepository.class), mock(DataSource.class));
    var date = service.normalizedDateForTest("02/30/2019");
    assertThat(date).isEqualTo(LocalDate.of(2019, 3, 2));
  }
}
