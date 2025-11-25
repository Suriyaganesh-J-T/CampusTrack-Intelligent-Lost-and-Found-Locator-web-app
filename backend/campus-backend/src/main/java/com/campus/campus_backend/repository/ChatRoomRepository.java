package com.campus.campus_backend.repository;

import com.campus.campus_backend.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // Find chat room by match ID (note the underscore)
    ChatRoom findByMatch_Id(Long matchId);

    // Find all active chat rooms for a user: (user1Id = ? OR user2Id = ?) AND status = ?
    List<ChatRoom> findByUser1IdOrUser2IdAndStatus(Long user1Id, Long user2Id, String status);
}
