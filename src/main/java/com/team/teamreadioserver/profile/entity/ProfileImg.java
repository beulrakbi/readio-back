package com.team.teamreadioserver.profile.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

@Entity
@Table(name="profile_img")
public class ProfileImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id", nullable = false)
    private Long imgId;

    @OneToOne
    @JoinColumn(name= "profile_id", referencedColumnName = "profile_id")
    private Profile profile;

    @Column(name= "original_name", length = 200)
    private String originalName;

    @Column(name = "save_name", length = 200)
    private String saveName;
}
