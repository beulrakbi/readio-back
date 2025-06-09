package com.team.teamreadioserver.video.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class VideoDTO {
    private String videoId;
    private String title;
    private String channelTitle;
    private String description;
    private String thumbnail;
    private int viewCount;
    private Date uploadDate;

}
