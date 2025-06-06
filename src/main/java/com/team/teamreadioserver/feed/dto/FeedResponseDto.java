package com.team.teamreadioserver.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedResponseDto {
    private List<FeedItemDto> feedItems;
    private int currentPage;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
}