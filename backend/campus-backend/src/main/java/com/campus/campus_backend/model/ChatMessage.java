package com.campus.campus_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id")
    private User sender;


    @ManyToOne @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @Column(nullable = false) private String content;
    @Column(name = "sent_at", nullable = false) private LocalDateTime sentAt;
}
