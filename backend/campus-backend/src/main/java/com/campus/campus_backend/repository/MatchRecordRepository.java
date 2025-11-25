package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.MatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRecordRepository extends JpaRepository<MatchRecord, Long> {

    // Find all match records for a user (either as lostUser or foundUser)
    List<MatchRecord> findByLostUserIdOrFoundUserId(Long lostUserId, Long foundUserId);

    // Find by lostPost and foundPost
    MatchRecord findByLostPostIdAndFoundPostId(Long lostPostId, Long foundPostId);

    // Find all match records for a specific post (check lostPost or foundPost)
    List<MatchRecord> findByLostPostIdOrFoundPostId(Long lostPostId, Long foundPostId);

    // Find all pending matches for a user
    List<MatchRecord> findByLostUserIdAndStatus(Long lostUserId, String status);

    // REPLACEMENT: find all matches for a post
    default List<MatchRecord> findAllByPostId(Long postId) {
        return findByLostPostIdOrFoundPostId(postId, postId);
    }
}
