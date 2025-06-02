package com.team.teamreadioserver.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
//클릭 통계용 DTO
public class ClickedContentDTO {
    private String contentId;       // videoId or bookIsbn
    private String contentType;     // "book" or "video"
    private String title;           // bookTitle or videoTitle
    private String thumbnail;       // bookCover or video.thumbnail
    private String source;          // author (for book) or channelTitle (for video)
    private Long clickCount;
}
