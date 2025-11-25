package com.campus.campus_backend.controller;

import com.campus.campus_backend.model.ChatRequest;
import com.campus.campus_backend.model.ChatRoom;
import com.campus.campus_backend.model.MatchRecord;
import com.campus.campus_backend.service.MatchRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private MatchRequestService matchRequestService;

    // -------------------------------
    // Send match request
    // -------------------------------
    @PostMapping("/request")
    public ResponseEntity<ChatRequest> sendRequest(
            @RequestParam Long matchId,
            @RequestParam Long senderId,
            @RequestParam Long receiverId
    ) {
        ChatRequest request = matchRequestService.sendRequest(matchId, senderId, receiverId);
        return ResponseEntity.ok(request);
    }

    // -------------------------------
    // Accept a match request
    // -------------------------------
    @PostMapping("/accept")
    public ResponseEntity<ChatRoom> acceptRequest(@RequestParam Long requestId) {
        ChatRoom chatRoom = matchRequestService.acceptRequest(requestId);
        return ResponseEntity.ok(chatRoom);
    }

    // -------------------------------
    // Decline a match request
    // -------------------------------
    @PostMapping("/decline")
    public ResponseEntity<String> declineRequest(@RequestParam Long requestId) {
        matchRequestService.declineRequest(requestId);
        return ResponseEntity.ok("Request declined successfully");
    }

    // -------------------------------
    // List pending requests for a user (loser)
    // -------------------------------
    @GetMapping("/pending")
    public ResponseEntity<List<ChatRequest>> getPendingRequests(@RequestParam Long userId) {
        List<ChatRequest> pending = matchRequestService.getPendingRequests(userId);
        return ResponseEntity.ok(pending);
    }

    // -------------------------------
    // Get matches for a post
    // -------------------------------
    @GetMapping("/for-post/{postId}")
    public ResponseEntity<List<MatchRecord>> getMatchesForPost(@PathVariable Long postId) {
        List<MatchRecord> matches = matchRequestService.getMatchesForPost(postId);
        return ResponseEntity.ok(matches);
    }

}
