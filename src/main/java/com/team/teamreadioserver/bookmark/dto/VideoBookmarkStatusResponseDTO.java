package com.team.teamreadioserver.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoBookmarkStatusResponseDTO {
    private boolean isBookmarked; // 현재 사용자가 북마크 했는지 여부
    private long totalBookmarkCount; // 전체 북마크 개수
    private Integer bookmarkId; // 현재 사용자의 북마크 ID (삭제 시 필요)
}
