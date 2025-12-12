package com.campus.campus_backend.service;

import com.campus.campus_backend.model.ChatMessage;
import com.campus.campus_backend.model.ChatRoom;
import com.campus.campus_backend.model.User;
import com.campus.campus_backend.repository.ChatMessageRepository;
import com.campus.campus_backend.repository.ChatRoomRepository;
import com.campus.campus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository messageRepo;
    private final ChatRoomRepository roomRepo;
    private final UserRepository userRepo;

    public ChatMessageService(ChatMessageRepository messageRepo,
                              ChatRoomRepository roomRepo,
                              UserRepository userRepo) {
        this.messageRepo = messageRepo;
        this.roomRepo = roomRepo;
        this.userRepo = userRepo;
    }

    public List<ChatMessage> getMessages(Long roomId) {
        return messageRepo.findByRoom_IdOrderBySentAtAsc(roomId);
    }

    // 🔥 Required Method for WebSocket Messaging
    public ChatMessage sendMessage(Long roomId, String senderId, String text) {

        ChatRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        ChatMessage msg = new ChatMessage();
        msg.setRoom(room);
        msg.setSender(sender);
        msg.setContent(text);
        msg.setSentAt(LocalDateTime.now());

        return messageRepo.save(msg);
    }
}
