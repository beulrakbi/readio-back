package com.team.teamreadioserver.statistics.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PopularVideoDTO {
    private String videoId;
    private String title;
    private String channelTitle;
    private String thumbnail;
    private Long clickCount;
}
