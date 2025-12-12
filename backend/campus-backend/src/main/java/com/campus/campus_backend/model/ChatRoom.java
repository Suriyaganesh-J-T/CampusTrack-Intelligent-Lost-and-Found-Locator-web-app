package com.campus.campus_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne @JoinColumn(name = "match_record_id")
    private MatchRecord matchRecord;

    @ManyToOne @JoinColumn(name = "user1_id", referencedColumnName = "user_id", nullable = false)
    private User user1;

    @ManyToOne @JoinColumn(name = "user2_id", referencedColumnName = "user_id", nullable = false)
    private User user2;

    @Column(nullable = false) private String status; // ACTIVE, CLOSED
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
}
