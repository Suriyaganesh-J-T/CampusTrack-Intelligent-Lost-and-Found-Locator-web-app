package com.campus.campus_backend.dto;

import com.campus.campus_backend.model.MatchRecord;
import lombok.Setter;

import java.time.LocalDateTime;

public class MatchRecordDTO {

    private Long id;
    private Double matchScore;
    private String status;
    private String lostUserId;
    private String lostUserName;
    private String foundUserId;
    private String foundUserName;
    private LocalDateTime createdAt;
    private Long chatRequestId;

    // Post info
    private Long lostPostId;
    private String lostPostName;
    private String lostPostType;
    private String lostPostPlace;

    private Long foundPostId;
    private String foundPostName;
    private String foundPostType;
    private String foundPostPlace;

    // 🔥 NEW FIELDS
    private Long chatId;

    @Setter
    private String displayStatus;

    public MatchRecordDTO(MatchRecord match, Long chatRequestId) {
        this.id = match.getId();
        this.matchScore = match.getMatchScore();
        this.status = match.getStatus();
        this.createdAt = match.getCreatedAt();
        this.chatRequestId = chatRequestId;

        if (match.getLostUser() != null) {
            this.lostUserId = match.getLostUser().getUserId();
            this.lostUserName = match.getLostUser().getName();
        }
        if (match.getFoundUser() != null) {
            this.foundUserId = match.getFoundUser().getUserId();
            this.foundUserName = match.getFoundUser().getName();
        }

        if (match.getLostPost() != null) {
            this.lostPostId = match.getLostPost().getId();
            this.lostPostName = match.getLostPost().getItemName();
            this.lostPostType = match.getLostPost().getType();
            this.lostPostPlace = match.getLostPost().getPlace();
        }

        if (match.getFoundPost() != null) {
            this.foundPostId = match.getFoundPost().getId();
            this.foundPostName = match.getFoundPost().getItemName();
            this.foundPostType = match.getFoundPost().getType();
            this.foundPostPlace = match.getFoundPost().getPlace();
        }
    }

    // Getters
    public Long getId() { return id; }
    public Double getMatchScore() { return matchScore; }
    public String getStatus() { return status; }
    public String getLostUserId() { return lostUserId; }
    public String getLostUserName() { return lostUserName; }
    public String getFoundUserId() { return foundUserId; }
    public String getFoundUserName() { return foundUserName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getChatRequestId() { return chatRequestId; }

    public Long getLostPostId() { return lostPostId; }
    public String getLostPostName() { return lostPostName; }
    public String getLostPostType() { return lostPostType; }
    public String getLostPostPlace() { return lostPostPlace; }

    public Long getFoundPostId() { return foundPostId; }
    public String getFoundPostName() { return foundPostName; }
    public String getFoundPostType() { return foundPostType; }
    public String getFoundPostPlace() { return foundPostPlace; }

    // NEW FIELD GETTER/SETTER
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }

    public String getDisplayStatus() { return displayStatus; }
}
