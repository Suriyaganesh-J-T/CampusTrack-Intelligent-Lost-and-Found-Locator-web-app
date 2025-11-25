package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.ChatMessage;
import com.campus.campus_backend.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Fetch all messages in a chat room, ordered by creation time
    List<ChatMessage> findByRoomOrderByCreatedAtAsc(ChatRoom room);

    // Optional: fetch messages by sender in a room (future use)
    // List<ChatMessage> findByRoomAndSenderOrderByCreatedAtAsc(ChatRoom room, User sender);

    // Fetch all messages of a room sorted by createdAt ascending
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(Long roomId);
}
