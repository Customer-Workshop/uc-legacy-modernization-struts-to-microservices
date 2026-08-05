package com.northstar.intake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.northstar.intake.dto.FnolRequest;
import com.northstar.intake.model.Claim;
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
  void lenientDateRollsFebruaryForward() {
    var service = new ClaimApplicationService(mock(ClaimRepository.class), mock(DataSource.class));
    var date = service.normalizedDateForTest("02/30/2019");
    assertThat(date).isEqualTo(LocalDate.of(2019, 3, 2));
  }

  @Test
  void blankOrUnparseableDateDefaultsToLegacyFixedDate() {
    var service = new ClaimApplicationService(mock(ClaimRepository.class), mock(DataSource.class));
    assertThat(service.normalizedDateForTest(null)).isEqualTo(LocalDate.of(2019, 4, 1));
    assertThat(service.normalizedDateForTest("")).isEqualTo(LocalDate.of(2019, 4, 1));
    assertThat(service.normalizedDateForTest("not-a-date")).isEqualTo(LocalDate.of(2019, 4, 1));
  }

  @Test
  void fnolHardcodesPolicyLossTypeAdjusterAndReporter() {
    var repository = mock(ClaimRepository.class);
    when(repository.nextId()).thenReturn(121);
    when(repository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));
    var service = new ClaimApplicationService(repository, mock(DataSource.class));

    Claim claim =
        service.create(new FnolRequest("Ann Claimant", "Pipe burst", "04/02/2019", "FIRE"));

    assertThat(claim.getPolicyId()).isEqualTo(9001);
    assertThat(claim.getLossType()).isEqualTo("WATER");
    assertThat(claim.getAssignedAdjuster()).isEqualTo("adjuster1");
    assertThat(claim.getCreatedBy()).isEqualTo("supervisor");
  }
}
