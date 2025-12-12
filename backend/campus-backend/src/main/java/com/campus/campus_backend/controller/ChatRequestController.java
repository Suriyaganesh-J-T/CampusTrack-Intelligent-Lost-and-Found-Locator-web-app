package com.campus.campus_backend.controller;

import com.campus.campus_backend.model.ChatRequest;
import com.campus.campus_backend.service.ChatRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat-requests")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ChatRequestController {

    private final ChatRequestService chatRequestService;

    public ChatRequestController(ChatRequestService chatRequestService) {
        this.chatRequestService = chatRequestService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatRequest> sendRequest(@RequestParam Long matchId,
                                                   @RequestParam String senderId,
                                                   @RequestParam String receiverId) {
        ChatRequest created = chatRequestService.createRequest(matchId, senderId, receiverId);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/status")
    public ResponseEntity<ChatRequest> setStatus(@RequestParam Long requestId, @RequestParam String status) {
        ChatRequest updated = chatRequestService.setStatus(requestId, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ChatRequest>> getPending(@RequestParam String userId) {
        return ResponseEntity.ok(chatRequestService.getPendingRequests(userId));
    }
}
