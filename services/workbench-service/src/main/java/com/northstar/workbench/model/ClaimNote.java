package com.northstar.workbench.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "claim_note")
public class ClaimNote {
  @Id private Integer noteId;
  private Integer claimId;
  private String author;
  private LocalDate noteDate;
  private String noteText;

  protected ClaimNote() {}

  public Integer getNoteId() {
    return noteId;
  }

  public Integer getClaimId() {
    return claimId;
  }

  public String getAuthor() {
    return author;
  }

  public LocalDate getNoteDate() {
    return noteDate;
  }

  public String getNoteText() {
    return noteText;
  }
}
