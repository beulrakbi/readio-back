package com.team.teamreadioserver.statistics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "click_log")
public class ClickLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "click_log_id")
    private Long clickLogId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "content_id")
    private String contentId;

    @Column(name = "content_type")
    private String contentType;

    @Builder.Default
    @Column(name = "clicked_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime clickedAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.clickedAt == null) {
            this.clickedAt = LocalDateTime.now();
        }
    }
}
