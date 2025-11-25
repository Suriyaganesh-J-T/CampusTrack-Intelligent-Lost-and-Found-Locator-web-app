// inside ChatRoomService class (add this method)
package com.campus.campus_backend.service;

import com.campus.campus_backend.model.ChatRoom;
import com.campus.campus_backend.model.ChatRequest;
import com.campus.campus_backend.repository.ChatRoomRepository;
import com.campus.campus_backend.repository.ChatRequestRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRequestRepository chatRequestRepository;
    private final SimpMessagingTemplate template;

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           ChatRequestRepository chatRequestRepository,
                           SimpMessagingTemplate template) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRequestRepository = chatRequestRepository;
        this.template = template;
    }

    @Transactional
    public ChatRoom createRoomFromRequest(Long requestId) {
        ChatRequest req = chatRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRequest not found: " + requestId));

        ChatRoom room = new ChatRoom();
        room.setMatch(req.getMatch());
        room.setUser1(req.getReceiver()); // assume receiver = loser user1
        room.setUser2(req.getSender());   // sender = founder
        room.setStatus("ACTIVE");

        ChatRoom saved = chatRoomRepository.save(room);

        // notify both users about roomId (optional)
        var dto = new java.util.HashMap<String, Object>();
        dto.put("roomId", saved.getId());
        dto.put("matchId", req.getMatch().getId());

        template.convertAndSend("/topic/match/status/" + req.getSender().getId(), dto);
        template.convertAndSend("/topic/match/status/" + req.getReceiver().getId(), dto);

        return saved;
    }
}
