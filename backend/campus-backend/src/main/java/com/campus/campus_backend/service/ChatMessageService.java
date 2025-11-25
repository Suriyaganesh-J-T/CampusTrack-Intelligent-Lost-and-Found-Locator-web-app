package com.campus.campus_backend.service;

import com.campus.campus_backend.model.ChatMessage;
import com.campus.campus_backend.model.ChatRoom;
import com.campus.campus_backend.model.User;
import com.campus.campus_backend.repository.ChatMessageRepository;
import com.campus.campus_backend.repository.ChatRoomRepository;
import com.campus.campus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository,
                              ChatRoomRepository chatRoomRepository,
                              UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all messages for a room
     */
    public List<ChatMessage> getMessages(Long roomId) {
        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    /**
     * Save a message in a chat room
     * Used by REST and WebSocket controllers
     */
    @Transactional
    public ChatMessage saveMessage(Long roomId, Long senderId, String content) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + senderId));

        ChatMessage message = new ChatMessage();
        message.setRoom(room);
        message.setSender(sender);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());

        return chatMessageRepository.save(message);
    }
}
