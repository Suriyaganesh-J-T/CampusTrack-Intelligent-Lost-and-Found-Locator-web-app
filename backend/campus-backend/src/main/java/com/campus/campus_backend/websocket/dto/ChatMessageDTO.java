package com.campus.campus_backend.websocket.dto;

import java.time.LocalDateTime;

public class ChatMessageDTO {
    private Long id;         // optional - filled after save
    private Long roomId;
    private Long senderId;   // filled by backend
    private Long receiverId; // optional, for private messages
    private String senderName; // filled by backend
    private String content;
    private LocalDateTime createdAt; // filled by backend

    public ChatMessageDTO() {}

    // Optional constructor to create DTO from entity
    public ChatMessageDTO(Long id, Long roomId, Long senderId, String senderName,
                          String content, LocalDateTime createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.createdAt = createdAt;
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
