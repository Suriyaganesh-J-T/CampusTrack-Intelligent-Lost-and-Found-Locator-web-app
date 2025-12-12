package com.campus.campus_backend.dto;

import com.campus.campus_backend.model.MatchRecord;
import com.campus.campus_backend.model.MatchRequest;

import java.time.LocalDateTime;

public class ChatRequestDTO {
    private Long id;
    private Long matchId;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String receiverName;
    private String status;
    private LocalDateTime createdAt;
    private String chatId;   // chat room id if created
    private String postSummary;

    public ChatRequestDTO(MatchRequest req) {
        this.id = req.getId();
        MatchRecord m = req.getMatch();
        this.matchId = m != null ? m.getId() : null;

        this.senderId = req.getSender().getUserId();
        this.senderName = req.getSender().getName();
        this.receiverId = req.getReceiver().getUserId();
        this.receiverName = req.getReceiver().getName();
        this.status = req.getStatus();
        this.createdAt = req.getCreatedAt();

        if (m != null) {
            String lostName = m.getLostPost() != null ? m.getLostPost().getItemName() : "Lost item";
            String foundName = m.getFoundPost() != null ? m.getFoundPost().getItemName() : "Found item";
            this.postSummary = "Lost: " + lostName + " • Found: " + foundName;
        }
    }

    public Long getId() { return id; }
    public Long getMatchId() { return matchId; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getReceiverId() { return receiverId; }
    public String getReceiverName() { return receiverName; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getPostSummary() { return postSummary; }
}
