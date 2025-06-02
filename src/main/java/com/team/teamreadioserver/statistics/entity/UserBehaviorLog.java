package com.team.teamreadioserver.statistics.entity;

import com.team.teamreadioserver.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_behavior_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBehaviorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "behavior_log_id")
    private Integer behaviorLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "section", nullable = false, length = 50)
    private String section;

    @Column(name = "stay_time")
    private Long stayTime;

    @Column(name = "click_count")
    private Integer clickCount;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
