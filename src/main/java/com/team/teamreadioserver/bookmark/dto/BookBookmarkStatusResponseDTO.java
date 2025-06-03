// src/main/java/com/team/teamreadioserver/bookmark/dto/BookBookmarkStatusResponseDTO.java
package com.team.teamreadioserver.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder; // @Builder 추가 (선택 사항이지만 편리)
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // @Builder 추가
public class BookBookmarkStatusResponseDTO {
    private boolean bookmarked; // 현재 사용자가 북마크 했는지 여부 (VideoBookmarkStatusResponseDTO의 isBookmarked와 매핑)
    private long totalBookmarkCount; // 전체 북마크 개수
    private Integer bookmarkId; // 현재 사용자의 북마크 ID (삭제 시 필요)
}