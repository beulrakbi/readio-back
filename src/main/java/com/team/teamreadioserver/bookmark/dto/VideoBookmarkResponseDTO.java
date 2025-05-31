package com.team.teamreadioserver.bookmark.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoBookmarkResponseDTO {
    private Integer bookmarkId;
    private String videoId;
    private String videoTitle;    // 추가
    private String channelTitle;  // 추가
    private String thumbnailUrl;  // 추가 (Video 엔티티의 thumbnail 필드를 직접 넘겨줄 경우)
}
