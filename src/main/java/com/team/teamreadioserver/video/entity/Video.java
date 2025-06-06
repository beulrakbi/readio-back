package com.team.teamreadioserver.video.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "video")
@Getter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Video {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "video_id")
    private String videoId;

    @Column(name = "title")
    private String title;

    @Column(name = "channel_title")
    private String channelTitle;

    @Column(name = "description")
    private String description;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "upload_date")
    private Date uploadDate;


}