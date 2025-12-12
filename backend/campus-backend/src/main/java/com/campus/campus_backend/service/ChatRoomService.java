package com.campus.campus_backend.service;

import com.campus.campus_backend.model.ChatRoom;
import com.campus.campus_backend.model.MatchRecord;
import com.campus.campus_backend.model.User;
import com.campus.campus_backend.repository.ChatRoomRepository;
import com.campus.campus_backend.repository.MatchRecordRepository;
import com.campus.campus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final UserRepository userRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           MatchRecordRepository matchRecordRepository,
                           UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.userRepository = userRepository;
    }

    public List<ChatRoom> getChatRoomsForUser(String userId) {
        return chatRoomRepository.findByUser1_UserIdOrUser2_UserId(userId, userId);
    }

    // 🔥 NEW METHOD — used to attach chatId in MatchRecordDTO
    public Long getChatRoomIdForMatch(Long matchId) {
        ChatRoom room = chatRoomRepository.findByMatchRecordId(matchId);
        return room != null ? room.getId() : null;
    }

    @Transactional
    public ChatRoom createRoom(Long matchRecordId, String user1Id, String user2Id) {
        MatchRecord match = matchRecordRepository.findById(matchRecordId)
                .orElseThrow(() -> new RuntimeException("Match record not found"));

        User user1 = userRepository.findById(user1Id).orElseThrow(() -> new RuntimeException("User1 not found"));
        User user2 = userRepository.findById(user2Id).orElseThrow(() -> new RuntimeException("User2 not found"));

        ChatRoom existing = chatRoomRepository.findByMatchRecordId(match.getId());
        if (existing != null) return existing;

        ChatRoom room = new ChatRoom();
        room.setMatchRecord(match);
        room.setUser1(user1);
        room.setUser2(user2);
        room.setStatus("ACTIVE");
        room.setCreatedAt(LocalDateTime.now());

        return chatRoomRepository.save(room);
    }

    public ChatRoom getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat Room not found: " + roomId));
    }
}
