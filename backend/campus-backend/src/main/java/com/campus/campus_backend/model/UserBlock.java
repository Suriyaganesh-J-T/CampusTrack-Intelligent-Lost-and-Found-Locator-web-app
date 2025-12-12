package com.campus.campus_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_blocks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserBlock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "blocker_id", referencedColumnName = "user_id")
    private User blocker;

    @ManyToOne @JoinColumn(name = "blocked_id", referencedColumnName = "user_id")
    private User blocked;
}
