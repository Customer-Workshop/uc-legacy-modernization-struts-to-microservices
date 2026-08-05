package com.northstar.workbench.dto;

import com.northstar.workbench.model.Claim;
import com.northstar.workbench.model.ClaimNote;
import java.util.List;

public final class WorkbenchDtos {
  private WorkbenchDtos() {}

  public record ClaimSummaryResponse(
      String claimId,
      String claimNumber,
      String claimantName,
      String lossDate,
      String reportedDate,
      String lossType,
      String description,
      String status,
      String reserveAmount,
      String assignedAdjuster) {
    public static ClaimSummaryResponse from(Claim c) {
      return new ClaimSummaryResponse(
          c.getClaimId().toString(),
          c.getClaimNumber(),
          c.getClaimantName(),
          c.getLossDate().toString(),
          c.getReportedDate().toString(),
          c.getLossType(),
          c.getDescription(),
          c.getStatus(),
          c.getReserveAmount().setScale(2).toPlainString(),
          c.getAssignedAdjuster());
    }
  }

  public record WorkbenchListResponse(Integer claimCount, List<ClaimSummaryResponse> claims) {}

  public record AssignRequest(String adjuster) {}

  public record AssignResponse(String claimId, String assignedAdjuster) {}

  public record StatusResponse(String claimId, String claimStatus) {}

  public record ReserveRequest(String reserveAmount) {}

  public record ReserveResponse(String claimId, String reserveAmount) {}

  public record NoteRequest(String noteText) {}

  public record NoteResponse(String claimId, String noteText) {}

  public record ClaimNoteResponse(String noteId, String author, String noteDate, String noteText) {
    public static ClaimNoteResponse from(ClaimNote n) {
      return new ClaimNoteResponse(
          n.getNoteId().toString(), n.getAuthor(), n.getNoteDate().toString(), n.getNoteText());
    }
  }

  public record NoteHistoryResponse(String claimId, List<ClaimNoteResponse> notes) {}

  public record HistoryResponse(String claimId, List<Object> entries) {}
}
