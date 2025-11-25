package com.campus.campus_backend.websocket;

import com.campus.campus_backend.model.ChatMessage;
import com.campus.campus_backend.service.ChatMessageService;
import com.campus.campus_backend.service.ChatRoomService;
import com.campus.campus_backend.websocket.dto.ChatMessageDTO;
import com.campus.campus_backend.security.JwtUtil;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate template;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final JwtUtil jwtUtil;

    public ChatWebSocketController(SimpMessagingTemplate template,
                                   ChatMessageService chatMessageService,
                                   ChatRoomService chatRoomService,
                                   JwtUtil jwtUtil) {
        this.template = template;
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
        this.jwtUtil = jwtUtil;
    }

    @MessageMapping("/chat.send")
    public void handleSend(@Payload ChatMessageDTO dto) {
        if (dto == null || dto.getRoomId() == null || dto.getContent() == null) return;

        // Get sender email from SecurityContextHolder
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long senderId = jwtUtil.getUserIdByEmail(email);

        ChatMessage saved = chatMessageService.saveMessage(dto.getRoomId(), senderId, dto.getContent());

        ChatMessageDTO out = new ChatMessageDTO();
        out.setId(saved.getId());
        out.setRoomId(saved.getRoom().getId());
        out.setSenderId(saved.getSender().getId());
        out.setSenderName(saved.getSender().getName());
        out.setContent(saved.getContent());
        out.setCreatedAt(saved.getCreatedAt());

        template.convertAndSend("/topic/chat/" + dto.getRoomId(), out);
    }

    @MessageMapping("/chat.private")
    public void handlePrivate(@Payload ChatMessageDTO dto) {
        if (dto == null || dto.getRoomId() == null || dto.getContent() == null || dto.getReceiverId() == null) return;

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long senderId = jwtUtil.getUserIdByEmail(email);

        ChatMessage saved = chatMessageService.saveMessage(dto.getRoomId(), senderId, dto.getContent());

        ChatMessageDTO out = new ChatMessageDTO();
        out.setId(saved.getId());
        out.setRoomId(saved.getRoom().getId());
        out.setSenderId(saved.getSender().getId());
        out.setSenderName(saved.getSender().getName());
        out.setContent(saved.getContent());
        out.setCreatedAt(saved.getCreatedAt());

        template.convertAndSendToUser(
                dto.getReceiverId().toString(),
                "/queue/private",
                out
        );
    }
}
