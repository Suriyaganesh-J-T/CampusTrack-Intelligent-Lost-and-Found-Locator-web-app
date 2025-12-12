package com.campus.campus_backend.service;

import com.campus.campus_backend.dto.ChatMessageDTO;
import com.campus.campus_backend.model.ChatMessage;
import com.campus.campus_backend.model.ChatRoom;
import com.campus.campus_backend.model.User;
import com.campus.campus_backend.repository.ChatMessageRepository;
import com.campus.campus_backend.repository.ChatRoomRepository;
import com.campus.campus_backend.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;


@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       ChatRoomRepository chatRoomRepository,
                       UserRepository userRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Fetch chat history for a room.
     */
    public List<ChatMessage> getHistory(Long roomId, String userId) {
        return chatMessageRepository.findByRoom_IdOrderBySentAtAsc(roomId);
    }

    /**
     * Persist a new message.
     */
    public ChatMessage sendMessage(Long roomId, String senderId, String content) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        ChatMessage msg = new ChatMessage();
        msg.setRoom(room);
        msg.setSender(sender);
        msg.setContent(content);
        msg.setSentAt(LocalDateTime.now());

        return chatMessageRepository.save(msg);
    }

    /**
     * Process incoming DTO and broadcast to subscribers.
     */
    public void processAndBroadcast(Long roomId, ChatMessageDTO dto) {
        ChatMessage saved = sendMessage(roomId, dto.getSenderId(), dto.getContent());

        ChatMessageDTO out = new ChatMessageDTO();
        out.setId(saved.getId());
        out.setRoomId(saved.getRoom().getId());
        out.setSenderId(saved.getSender().getUserId());
        out.setSenderName(saved.getSender().getName());
        out.setContent(saved.getContent());
        dto.setSentAt(
                saved.getSentAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );


        // Broadcast to all subscribers of /topic/chat/{roomId}
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, out);
    }
}
