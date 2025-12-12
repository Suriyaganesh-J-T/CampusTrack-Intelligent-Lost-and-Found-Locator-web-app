package com.campus.campus_backend.controller;

import com.campus.campus_backend.model.ChatMessage;
import com.campus.campus_backend.security.UserDetailsImpl;
import com.campus.campus_backend.service.ChatMessageService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Getter
    public static class MessageResponseDTO {
        private Long id;
        private String senderId;
        private String senderName;
        private String content;
        private LocalDateTime sentAt;

        public MessageResponseDTO(ChatMessage msg) {
            this.id = msg.getId();
            this.senderId = msg.getSender().getUserId();
            this.senderName = msg.getSender().getName();
            this.content = msg.getContent();
            this.sentAt = msg.getSentAt();
        }
    }

    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<MessageResponseDTO>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<MessageResponseDTO> messages = chatMessageService.getMessages(roomId)
                .stream()
                .map(MessageResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(messages);
    }

    @PostMapping("/room/{roomId}/send")
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MessageRequest request
    ) {
        if (userDetails == null || userDetails.getUser() == null) {
            return ResponseEntity.status(401).build();
        }

        String senderId = userDetails.getUser().getUserId();
        ChatMessage msg = chatMessageService.sendMessage(roomId, senderId, request.getMessage());


        return ResponseEntity.ok(new MessageResponseDTO(msg));
    }

    static class MessageRequest {
        private String message;
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
