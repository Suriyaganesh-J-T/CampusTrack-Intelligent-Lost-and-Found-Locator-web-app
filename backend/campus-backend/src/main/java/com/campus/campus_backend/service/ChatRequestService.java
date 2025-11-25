package com.campus.campus_backend.service;

import com.campus.campus_backend.model.ChatRequest;
import com.campus.campus_backend.model.MatchRecord;
import com.campus.campus_backend.model.User;
import com.campus.campus_backend.repository.ChatRequestRepository;
import com.campus.campus_backend.repository.MatchRecordRepository;
import com.campus.campus_backend.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatRequestService {

    private final ChatRequestRepository chatRequestRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate template;

    public ChatRequestService(ChatRequestRepository chatRequestRepository,
                              MatchRecordRepository matchRecordRepository,
                              UserRepository userRepository,
                              SimpMessagingTemplate template) {
        this.chatRequestRepository = chatRequestRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.userRepository = userRepository;
        this.template = template;
    }

    /**
     * Create a chat request for a match record.
     * Notify the receiver in real-time via WebSocket: /topic/match/request/{receiverId}
     */
    @Transactional
    public ChatRequest createRequest(Long matchId, Long senderId, Long receiverId) {
        MatchRecord match = matchRecordRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("MatchRecord not found: " + matchId));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + senderId));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found: " + receiverId));

        ChatRequest req = new ChatRequest();
        req.setMatch(match);
        req.setSender(sender);
        req.setReceiver(receiver);
        req.setStatus("PENDING");

        ChatRequest saved = chatRequestRepository.save(req);

        // Build minimal DTO to send to client (you can expand later)
        var dto = new java.util.HashMap<String, Object>();
        dto.put("id", saved.getId());
        dto.put("matchId", match.getId());
        dto.put("matchScore", match.getMatchScore());
        dto.put("senderId", sender.getId());
        dto.put("senderName", sender.getName());
        dto.put("createdAt", saved.getCreatedAt());
        dto.put("status", saved.getStatus());

        // Notify the receiver instantly
        template.convertAndSend("/topic/match/request/" + receiverId, dto);

        return saved;
    }

    /**
     * Accept a chat request: mark ACCEPTED, create ChatRoom (delegating to ChatRoomService externally),
     * send status update to both parties via /topic/match/status/{userId}
     */
    @Transactional
    public ChatRequest setStatus(Long requestId, String status) {
        ChatRequest req = chatRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("ChatRequest not found: " + requestId));

        req.setStatus(status);
        ChatRequest saved = chatRequestRepository.save(req);

        var dto = new java.util.HashMap<String, Object>();
        dto.put("id", saved.getId());
        dto.put("status", saved.getStatus());
        dto.put("matchId", saved.getMatch().getId());

        // notify sender and receiver
        template.convertAndSend("/topic/match/status/" + saved.getSender().getId(), dto);
        template.convertAndSend("/topic/match/status/" + saved.getReceiver().getId(), dto);

        return saved;
    }

    public List<ChatRequest> getPendingRequests(Long receiverId) {
        return chatRequestRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(receiverId, "PENDING");
    }
}
