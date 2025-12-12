package com.campus.campus_backend.model;
import jakarta.persistence.*;
import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name = "chat_requests")
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class ChatRequest
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; @ManyToOne(optional = false)
private MatchRecord match; @ManyToOne(optional = false)
private User sender; @ManyToOne(optional = false)
private User receiver; @Column(nullable = false)
private String status; // PENDING, ACCEPTED, DECLINED
@Column(name = "sent_at", nullable = false)
private LocalDateTime sentAt = LocalDateTime.now();
}