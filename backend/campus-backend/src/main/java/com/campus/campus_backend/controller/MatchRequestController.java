package com.campus.campus_backend.controller;

import com.campus.campus_backend.model.ChatRoom;
import com.campus.campus_backend.model.MatchRequest;
import com.campus.campus_backend.model.MatchRecord;
import com.campus.campus_backend.model.User;
import com.campus.campus_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/chat-match", "/api/match"})
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class MatchRequestController {

    private final MatchRequestRepository requestRepo;
    private final MatchRecordRepository matchRepo;
    private final UserRepository userRepo;
    private final ChatRoomRepository roomRepo;

    public MatchRequestController(
            MatchRequestRepository requestRepo,
            MatchRecordRepository matchRepo,
            UserRepository userRepo,
            ChatRoomRepository roomRepo
    ) {
        this.requestRepo = requestRepo;
        this.matchRepo = matchRepo;
        this.userRepo = userRepo;
        this.roomRepo = roomRepo;
    }

    // ✅ NEW: Fetch incoming pending requests for logged-in user
    @GetMapping("/pending")
    public ResponseEntity<?> getPending(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        String userId = principal.getName();

        List<MatchRequest> list = requestRepo.findByReceiver_UserId(userId)
                .stream()
                .filter(r -> "PENDING".equals(r.getStatus()))
                .toList();

        return ResponseEntity.ok(list);
    }

    // Send Request
    @PostMapping("/request/{matchId}")
    public ResponseEntity<?> sendRequest(@PathVariable Long matchId,
                                         @RequestParam String receiverId,
                                         Principal principal) {

        if (principal == null || principal.getName() == null)
            return ResponseEntity.status(401).body(Map.of("error", "Unauthenticated"));

        String senderId = principal.getName();

        MatchRecord mr = matchRepo.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepo.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (senderId.equals(receiverId))
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot send request to yourself"));

        boolean isParticipant =
                (mr.getLostUser() != null && senderId.equals(mr.getLostUser().getUserId())) ||
                        (mr.getFoundUser() != null && senderId.equals(mr.getFoundUser().getUserId()));

        if (!isParticipant)
            return ResponseEntity.status(403).body(Map.of("error", "You are not part of this match"));

        MatchRequest req = new MatchRequest();
        req.setSender(sender);
        req.setReceiver(receiver);
        req.setMatch(mr);
        req.setStatus("PENDING");
        req.setCreatedAt(LocalDateTime.now());
        requestRepo.save(req);

        mr.setStatus("REQUEST_SENT");
        matchRepo.save(mr);

        return ResponseEntity.ok(Map.of("message", "Request Sent", "requestId", req.getId()));
    }

    // Accept Request
    @PostMapping("/accept")
    @Transactional
    public ResponseEntity<?> approve(@RequestParam Long requestId, Principal principal) {

        if (principal == null) return ResponseEntity.status(401).build();
        String userId = principal.getName();

        MatchRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!userId.equals(req.getSender().getUserId()) &&
                !userId.equals(req.getReceiver().getUserId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Not authorized"));
        }

        req.setStatus("ACCEPTED");
        requestRepo.save(req);

        MatchRecord record = req.getMatch();
        record.setStatus("CHATTING");
        matchRepo.save(record);

        ChatRoom existing = roomRepo.findByMatchRecordId(record.getId());
        if (existing != null)
            return ResponseEntity.ok(Map.of("chatId", existing.getId()));

        ChatRoom room = new ChatRoom();
        room.setMatchRecord(record);
        room.setUser1(req.getSender());
        room.setUser2(req.getReceiver());
        room.setStatus("ACTIVE");
        room.setCreatedAt(LocalDateTime.now());

        ChatRoom saved = roomRepo.save(room);

        return ResponseEntity.ok(Map.of("chatId", saved.getId()));
    }

    // Decline Request
    @PostMapping("/decline")
    public ResponseEntity<?> decline(@RequestParam Long requestId, Principal principal) {

        if (principal == null) return ResponseEntity.status(401).build();
        String userId = principal.getName();

        MatchRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!userId.equals(req.getSender().getUserId()) &&
                !userId.equals(req.getReceiver().getUserId())) {
            return ResponseEntity.status(403).body(Map.of("error", "Not authorized"));
        }

        req.setStatus("DECLINED");
        requestRepo.save(req);

        return ResponseEntity.ok(Map.of("message", "Request Declined"));
    }
}
