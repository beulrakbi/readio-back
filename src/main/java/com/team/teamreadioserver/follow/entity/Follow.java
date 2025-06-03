package com.team.teamreadioserver.follow.entity;

import com.team.teamreadioserver.profile.entity.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "follow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private int followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_profile_id")
    private Profile following;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_profile_id")
    private Profile follower;

    public Follow(Profile follower, Profile following) {
        this.follower = follower;
        this.following = following;
    }
}
