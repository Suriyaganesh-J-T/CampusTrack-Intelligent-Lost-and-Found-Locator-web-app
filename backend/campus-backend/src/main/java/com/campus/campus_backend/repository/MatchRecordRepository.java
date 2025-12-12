package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.MatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface MatchRecordRepository extends JpaRepository<MatchRecord, Long> {
    List<MatchRecord> findByLostPost_IdOrFoundPost_Id(Long lostPostId, Long foundPostId);
    Optional<MatchRecord> findByLostUser_UserIdAndFoundUser_UserId(String lostUserId, String foundUserId);
}
