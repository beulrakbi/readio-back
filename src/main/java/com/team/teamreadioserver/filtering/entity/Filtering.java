package com.team.teamreadioserver.filtering.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "filtering")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Filtering {

    @Id
    @Column(name = "filtering_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int filteringId;

    @Column(name = "group_id")
    private int groupId;

    @Column(name = "video_id")
    @EqualsAndHashCode.Include
    private String videoId;

    @Column(name = "keyword")
    @EqualsAndHashCode.Include
    private String keyword;

    public void modifyFilter(String newVideoId, String newKeyword)
    {
        this.videoId = newVideoId;
        this.keyword = newKeyword;
    }


}
