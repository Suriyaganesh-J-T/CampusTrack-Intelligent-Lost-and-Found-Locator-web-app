package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByMatchRecordId(Long matchRecordId);
    java.util.List<ChatRoom> findByUser1_UserIdOrUser2_UserId(String user1, String user2);
}
