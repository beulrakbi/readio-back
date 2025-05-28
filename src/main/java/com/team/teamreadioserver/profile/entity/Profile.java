package com.team.teamreadioserver.profile.entity;

import com.team.teamreadioserver.profile.enums.PrivateStatus;
import com.team.teamreadioserver.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

@Entity
@Table(name="profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", updatable = false, nullable = false)
    private Long profileId;

    @OneToOne
    @JoinColumn(name="user_id", referencedColumnName ="user_id")
    private User user;

    @Size(max = 300, message = "서재소개는 최대 300자까지 가능합니다.")
    @Column(name = "biography", length = 300)
    private String biography;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_private", nullable = false)
    private PrivateStatus isPrivate = PrivateStatus.PUBLIC;

    @Size(max = 50, message = "필명은 최대 50자까지 가능합니다.")
    @Column(name = "pen_name", length = 50)
    private String penName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}