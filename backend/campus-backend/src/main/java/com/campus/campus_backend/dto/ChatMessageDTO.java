package com.campus.campus_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {
    private Long id;
    private Long roomId;
    private String senderId;
    private String senderName;
    private String content;
    private String sentAt;
    private String receiverId;

}
