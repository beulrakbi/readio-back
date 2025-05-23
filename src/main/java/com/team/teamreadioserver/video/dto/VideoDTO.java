package com.team.teamreadioserver.video.dto;

import lombok.*;

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

}
