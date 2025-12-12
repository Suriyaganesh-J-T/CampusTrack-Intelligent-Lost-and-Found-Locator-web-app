package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoom_IdOrderBySentAtAsc(Long roomId);
}
