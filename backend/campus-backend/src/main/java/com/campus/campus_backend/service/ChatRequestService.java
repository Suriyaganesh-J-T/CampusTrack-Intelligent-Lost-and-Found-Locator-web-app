package com.campus.campus_backend.service;

import com.campus.campus_backend.model.*;
import com.campus.campus_backend.repository.*;
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

    @Transactional
    public ChatRequest createRequest(Long matchId, String senderId, String receiverId) {
        MatchRecord match = matchRecordRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("MatchRecord not found: " + matchId));

        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender not found: " + senderId));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Receiver not found: " + receiverId));

        boolean exists = chatRequestRepository.findByMatch_Id(matchId).stream()
                .anyMatch(r -> r.getSender().getUserId().equals(senderId) &&
                        r.getReceiver().getUserId().equals(receiverId) &&
                        "PENDING".equals(r.getStatus()));
        if (exists) throw new IllegalStateException("Request already exists");

        ChatRequest req = new ChatRequest();
        req.setMatch(match);
        req.setSender(sender);
        req.setReceiver(receiver);
        req.setStatus("PENDING");

        ChatRequest saved = chatRequestRepository.save(req);

        var dto = new java.util.HashMap<String, Object>();
        dto.put("id", saved.getId());
        dto.put("matchId", match.getId());
        dto.put("matchScore", match.getMatchScore());
        dto.put("senderId", sender.getUserId());
        dto.put("senderName", sender.getName());
        dto.put("status", saved.getStatus());

        template.convertAndSendToUser(receiverId, "/queue/match/request", dto);
        return saved;
    }

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

        template.convertAndSendToUser(saved.getSender().getUserId(), "/queue/match/status", dto);
        template.convertAndSendToUser(saved.getReceiver().getUserId(), "/queue/match/status", dto);
        return saved;
    }

    public List<ChatRequest> getPendingRequests(String receiverId) {
        return chatRequestRepository.findByReceiver_UserId(receiverId)
                .stream().filter(r -> "PENDING".equals(r.getStatus())).toList();
    }
}
