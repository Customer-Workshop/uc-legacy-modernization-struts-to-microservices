package com.northstar.intake.service;

import com.northstar.intake.dto.FnolRequest;
import com.northstar.intake.exception.NotFoundException;
import com.northstar.intake.model.Claim;
import com.northstar.intake.repository.ClaimRepository;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClaimApplicationService {
  private final ClaimRepository repository;
  private final DataSource dataSource;

  public ClaimApplicationService(ClaimRepository repository, DataSource dataSource) {
    this.repository = repository;
    this.dataSource = dataSource;
  }

  public List<String> validate(FnolRequest request) {
    var errors = new java.util.ArrayList<String>();
    if (request.claimantName() == null || request.claimantName().trim().isEmpty())
      errors.add("errors.claimant.required");
    if (request.description() == null || request.description().trim().isEmpty())
      errors.add("errors.description.required");
    if (request.lossDate() != null
        && !request.lossDate().isEmpty()
        && !request.lossDate().matches("\\d{2}/\\d{2}/\\d{4}"))
      errors.add("errors.lossdate.format");
    return errors;
  }

  @Transactional
  public Claim create(FnolRequest request) {
    LocalDate lossDate = normalizedDate(request.lossDate());
    int id = repository.nextId();
    Claim claim =
        new Claim(
            id,
            "CLM-" + id,
            9001,
            request.claimantName(),
            lossDate,
            lossDate,
            "WATER",
            request.description(),
            "OPEN",
            new java.math.BigDecimal("0"),
            "adjuster1",
            "supervisor",
            lossDate);
    return repository.save(claim);
  }

  public Claim get(int id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("claim.notFound"));
  }

  @Transactional
  public void reset() {
    repository.deleteAllInBatch();
    new ResourceDatabasePopulator(new ClassPathResource("db/reset-claims.sql")).execute(dataSource);
  }

  private LocalDate normalizedDate(String source) {
    if (source == null || source.isEmpty()) {
      // legacy-faithful: blank or garbled dates default to the fixed legacy date.
      return LocalDate.of(2019, 4, 1);
    }
    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    format.setLenient(true);
    Date parsed = format.parse(source, new ParsePosition(0));
    if (parsed == null) return LocalDate.of(2019, 4, 1);
    return parsed.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
  }

  public LocalDate normalizedDateForTest(String source) {
    return normalizedDate(source);
  }
}
