package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.ChatRequest;
import com.campus.campus_backend.model.MatchRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {
    List<ChatRequest> findByMatch_Id(Long matchId);
    List<ChatRequest> findByReceiver_UserId(String receiverUserId);

}


