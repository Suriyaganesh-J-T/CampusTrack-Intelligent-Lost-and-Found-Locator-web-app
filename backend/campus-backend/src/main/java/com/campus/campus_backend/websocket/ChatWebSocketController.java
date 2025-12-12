package com.campus.campus_backend.websocket;

import com.campus.campus_backend.model.ChatMessage;
import com.campus.campus_backend.service.ChatMessageService;
import com.campus.campus_backend.dto.ChatMessageDTO;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate template;
    private final ChatMessageService chatMessageService;

    public ChatWebSocketController(SimpMessagingTemplate template,
                                   ChatMessageService chatMessageService) {
        this.template = template;
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat.send/{roomId}")
    public void handleSend(@DestinationVariable Long roomId,
                           @Payload ChatMessageDTO dto) {

        if (dto == null || dto.getContent() == null || dto.getSenderId() == null)
            return;

        ChatMessage saved = chatMessageService.sendMessage(
                roomId,
                dto.getSenderId(),
                dto.getContent().trim()
        );

        ChatMessageDTO out = new ChatMessageDTO();
        out.setId(saved.getId());                          // UNIQUE message ID
        out.setRoomId(saved.getRoom().getId());
        out.setSenderId(saved.getSender().getUserId());
        out.setSenderName(saved.getSender().getName());
        out.setContent(saved.getContent());

        // Convert LocalDateTime → String for frontend
        out.setSentAt(saved.getSentAt().toString());

        // ONE broadcast → no duplicates
        template.convertAndSend("/topic/chat/" + roomId, out);
    }
}
