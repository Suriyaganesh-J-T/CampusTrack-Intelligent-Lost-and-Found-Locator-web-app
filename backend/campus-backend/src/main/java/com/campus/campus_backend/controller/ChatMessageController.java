package com.campus.campus_backend.controller;

import com.campus.campus_backend.model.ChatMessage;
import com.campus.campus_backend.service.ChatMessageService;
import com.campus.campus_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private JwtUtil jwtUtil;

    public static class MessageDTO {
        private Long id;
        private Long senderId;
        private String senderName;
        private String content;
        private LocalDateTime createdAt;

        public MessageDTO(ChatMessage msg) {
            this.id = msg.getId();
            this.senderId = msg.getSender().getId();
            this.senderName = msg.getSender().getName();
            this.content = msg.getContent();
            this.createdAt = msg.getCreatedAt();
        }

        public Long getId() { return id; }
        public Long getSenderId() { return senderId; }
        public String getSenderName() { return senderName; }
        public String getContent() { return content; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    @GetMapping("/room/{roomId}/messages")
    public List<MessageDTO> getMessages(@PathVariable Long roomId,
                                        @RequestHeader("Authorization") String token) {

        String jwt = token.replace("Bearer ", "");
        Long userId = jwtUtil.validateTokenAndGetUserId(jwt); // ensures token valid

        List<ChatMessage> messages = chatMessageService.getMessages(roomId);
        return messages.stream().map(MessageDTO::new).collect(Collectors.toList());
    }

    @PostMapping("/room/{roomId}/send")
    public MessageDTO sendMessage(@PathVariable Long roomId,
                                  @RequestHeader("Authorization") String token,
                                  @RequestBody MessageRequest request) {

        String jwt = token.replace("Bearer ", "");
        Long senderId = jwtUtil.validateTokenAndGetUserId(jwt);

        // Save message using senderId + content
        ChatMessage msg = chatMessageService.saveMessage(roomId, senderId, request.getMessage());

        return new MessageDTO(msg);
    }

    static class MessageRequest {
        private String message;
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
