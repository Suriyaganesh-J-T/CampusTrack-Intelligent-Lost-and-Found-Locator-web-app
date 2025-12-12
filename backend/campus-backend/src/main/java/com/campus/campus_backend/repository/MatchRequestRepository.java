package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.MatchRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    List<MatchRequest> findByReceiver_UserId(String userId);
    List<MatchRequest> findByMatch_Id(Long matchId);

    @Query("SELECT r FROM MatchRequest r WHERE r.match.id = :matchId AND r.status = 'APPROVED'")
    MatchRequest findApprovedRequest(@Param("matchId") Long matchId);
}
