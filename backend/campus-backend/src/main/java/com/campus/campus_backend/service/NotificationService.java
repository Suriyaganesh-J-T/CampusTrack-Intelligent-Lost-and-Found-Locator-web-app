package com.campus.campus_backend.service;

import com.campus.campus_backend.model.MatchRecord;
import com.campus.campus_backend.model.MatchRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Existing: new auto-match created
    public void notifyNewMatch(String userId, MatchRecord matchRecord) {
        if (userId == null) return;
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/match/new",
                matchRecord
        );
    }

    // 🔥 NEW: any time a match request is sent / accepted / declined
    public void notifyRequestUpdated(MatchRequest request) {
        if (request == null || request.getMatch() == null) return;

        Long matchId = request.getMatch().getId();

        // We just send the match ID as payload – frontend only uses it as a signal.
        messagingTemplate.convertAndSendToUser(
                request.getSender().getUserId(),
                "/queue/match/request",
                matchId
        );
        messagingTemplate.convertAndSendToUser(
                request.getReceiver().getUserId(),
                "/queue/match/request",
                matchId
        );
    }
}
