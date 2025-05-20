package com.team.teamreadioserver.video.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class VideoDTO {
    private int videoId;
    private String title;
    private int channelTitle;
    private String description;
    private String thumbnail;

}
