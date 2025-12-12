package com.campus.campus_backend.service;

import com.campus.campus_backend.model.MatchRequest;
import com.campus.campus_backend.model.MatchRecord;
import com.campus.campus_backend.model.ChatRoom;
import com.campus.campus_backend.model.User;
import com.campus.campus_backend.repository.MatchRequestRepository;
import com.campus.campus_backend.repository.MatchRecordRepository;
import com.campus.campus_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MatchRequestService {

    private final MatchRequestRepository matchRequestRepository;
    private final MatchRecordRepository matchRecordRepository;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;
    private final NotificationService notificationService;

    public MatchRequestService(MatchRequestRepository matchRequestRepository,
                                 MatchRecordRepository matchRecordRepository,
                               UserRepository userRepository,
                               ChatRoomService chatRoomService,
                               NotificationService notificationService) {
        this.matchRequestRepository = matchRequestRepository;
        this.matchRecordRepository = matchRecordRepository;
        this.userRepository = userRepository;
        this.chatRoomService = chatRoomService;
        this.notificationService = notificationService;
    }

    @Transactional
    public MatchRequest sendRequest(Long matchRecordId, String senderId, String receiverId) {
        MatchRecord match = matchRecordRepository.findById(matchRecordId)
                .orElseThrow(() -> new RuntimeException("MatchRecord not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        MatchRequest request = new MatchRequest();
        request.setMatch(match);
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus("PENDING");

        MatchRequest saved = matchRequestRepository.save(request);

        // 🔔 notify both parties that a new request exists
        notificationService.notifyRequestUpdated(saved);

        return saved;
    }

    @Transactional
    public ChatRoom acceptRequest(Long requestId, String userId) {
        MatchRequest request = matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("MatchRequest not found"));

        // ensure only people in the request can call this
        if (!userId.equals(request.getSender().getUserId())
                && !userId.equals(request.getReceiver().getUserId())) {
            throw new SecurityException("You are not part of this match request.");
        }

        request.setStatus("ACCEPTED");
        matchRequestRepository.save(request);

        Long matchRecordId = request.getMatch().getId();

        // create or reuse a room for this match
        ChatRoom room = chatRoomService.createRoom(
                matchRecordId,
                request.getSender().getUserId(),
                request.getReceiver().getUserId()
        );

        // 🔔 notify both users so their Matches page refreshes
        notificationService.notifyRequestUpdated(request);

        return room;
    }

    @Transactional
    public void declineRequest(Long requestId, String userId) {
        MatchRequest request = matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("MatchRequest not found"));

        if (!userId.equals(request.getSender().getUserId())
                && !userId.equals(request.getReceiver().getUserId())) {
            throw new SecurityException("You are not part of this match request.");
        }

        request.setStatus("DECLINED");
        matchRequestRepository.save(request);

        // 🔔 notify both so UI updates
        notificationService.notifyRequestUpdated(request);
    }

    public List<MatchRequest> getPendingRequests(String userId) {
        return matchRequestRepository.findByReceiver_UserId(userId)
                .stream()
                .filter(req -> "PENDING".equals(req.getStatus()))
                .toList();
    }

    // Optional helper for other uses (not used directly by frontend anymore)
    public List<MatchRequest> getRequestsForMatch(Long matchRecordId) {
        return matchRequestRepository.findByMatch_Id(matchRecordId);
    }

    public Long getChatRequestIdForUser(MatchRecord match, String userId) {
        if (userId == null) return null;
        return matchRequestRepository.findByMatch_Id(match.getId())
                .stream()
                .filter(req -> userId.equals(req.getSender().getUserId())
                        || userId.equals(req.getReceiver().getUserId()))
                .map(MatchRequest::getId)
                .findFirst()
                .orElse(null);
    }

    public String getStatusForUser(MatchRecord match, String userId) {
        if (userId == null) return "NONE";

        Optional<MatchRequest> opt = matchRequestRepository.findByMatch_Id(match.getId())
                .stream()
                .filter(req -> userId.equals(req.getSender().getUserId())
                        || userId.equals(req.getReceiver().getUserId()))
                .findFirst();

        if (opt.isEmpty()) return "NONE";

        MatchRequest req = opt.get();
        String s = req.getStatus();

        if ("ACCEPTED".equals(s)) return "APPROVED";
        if ("DECLINED".equals(s)) return "DECLINED";

        // PENDING
        if (userId.equals(req.getSender().getUserId())) return "REQUEST_SENT";
        return "INCOMING_REQUEST";
    }
}
