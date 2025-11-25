package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.ChatRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {

    // Find pending requests for a receiver
    List<ChatRequest> findByReceiverIdAndStatusOrderByCreatedAtDesc(Long receiverId, String status);

    // Find by match record
    List<ChatRequest> findByMatchId(Long matchId);

    List<ChatRequest> findByReceiverIdAndStatus(Long receiverId, String status);

}
