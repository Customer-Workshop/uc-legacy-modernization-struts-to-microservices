package com.northstar.workbench.repository;

import com.northstar.workbench.model.ClaimNote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimNoteRepository extends JpaRepository<ClaimNote, Integer> {
  List<ClaimNote> findByClaimIdOrderByNoteDate(int claimId);
}
