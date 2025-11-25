package com.campus.campus_backend.service;

import com.campus.campus_backend.model.ChatRequest;
import com.campus.campus_backend.model.ChatRoom;
import com.campus.campus_backend.model.MatchRecord;
import com.campus.campus_backend.repository.ChatRequestRepository;
import com.campus.campus_backend.repository.MatchRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchRequestService {

    @Autowired
    private MatchRecordRepository matchRecordRepository;

    @Autowired
    private ChatRequestRepository chatRequestRepository;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private NotificationService notificationService;

    public List<ChatRequest> getPendingRequests(Long userId) {
        return chatRequestRepository.findByReceiverIdAndStatus(userId, "PENDING");
    }

    public List<MatchRecord> getMatchesForPost(Long postId) {
        return matchRecordRepository.findAllByPostId(postId);
    }



    // -------------------------
    // SEND MATCH REQUEST
    // -------------------------
    public ChatRequest sendRequest(Long matchId, Long senderId, Long receiverId) {

        MatchRecord match = matchRecordRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("MatchRecord not found"));

        ChatRequest request = new ChatRequest();
        request.setMatch(match);
        request.setSender(match.getFoundUser());
        request.setReceiver(match.getLostUser());
        request.setStatus("PENDING");

        match.setStatus("REQUEST_SENT");
        matchRecordRepository.save(match);

        ChatRequest saved = chatRequestRepository.save(request);

        // ✔ Notify receiver instantly
        notificationService.sendNewRequest(saved);

        return saved;
    }


    // -------------------------
    // ACCEPT REQUEST
    // -------------------------
    public ChatRoom acceptRequest(Long requestId) {

        ChatRequest request = chatRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("ACCEPTED");
        chatRequestRepository.save(request);

        MatchRecord match = request.getMatch();
        match.setStatus("ACCEPTED");
        matchRecordRepository.save(match);

        // ✔ notify sender
        notificationService.sendStatusUpdate(request);

        // FIXED ✔
        if (request.getStatus().equals("ACCEPTED")) {
            return chatRoomService.createRoomFromRequest(request.getId());
        }

        return null;
    }



    // -------------------------
    // DECLINE REQUEST
    // -------------------------
    public void declineRequest(Long requestId) {

        ChatRequest request = chatRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus("DECLINED");
        chatRequestRepository.save(request);

        MatchRecord match = request.getMatch();
        match.setStatus("DECLINED");
        matchRecordRepository.save(match);

        // ✔ notify sender
        notificationService.sendStatusUpdate(request);
    }
}
