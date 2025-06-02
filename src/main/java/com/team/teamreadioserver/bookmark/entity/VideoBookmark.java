package com.team.teamreadioserver.bookmark.entity;

import com.team.teamreadioserver.video.entity.Video;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", // video_bookmark 테이블의 외래키 컬럼명
            referencedColumnName = "video_id", // Video 테이블의 참조 대상 컬럼명
            nullable = false)
    private Video video;

    @Column(name= ("user_id"))
    private String userId;
}
