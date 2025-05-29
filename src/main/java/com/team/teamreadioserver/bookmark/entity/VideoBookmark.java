package com.team.teamreadioserver.bookmark.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ("bookmark_video"))
@Getter
public class VideoBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name=("bookmark_id"))
    private Integer bookmarkId;

    @Column(name = ("video_id"))
    private String videoId;

    @Column(name= ("user_id"))
    private String userId;
}
