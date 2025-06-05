package com.team.teamreadioserver.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkStatsDTO {
    private String contentId;
    private String title;
    private Long bookmarkCount;
}