package com.campus.campus_backend.service;

import com.campus.campus_backend.model.ChatRequest;
import com.campus.campus_backend.model.MatchRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNewRequest(ChatRequest request) {
        Long receiverId = request.getReceiver().getId();

        messagingTemplate.convertAndSend(
                "/topic/match/request/" + receiverId,
                request
        );
    }

    public void sendStatusUpdate(ChatRequest request) {
        Long senderId = request.getSender().getId();

        messagingTemplate.convertAndSend(
                "/topic/match/status/" + senderId,
                request
        );
    }
}
