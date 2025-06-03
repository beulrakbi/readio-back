package com.team.teamreadioserver.bookmark.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookBookmarkResponseDTO {
    private Integer bookmarkId;
    private String bookIsbn;
    private String bookTitle;   // Book 엔티티의 bookTitle 필드와 일치
    private String bookAuthor;  // Book 엔티티의 bookAuthor 필드와 일치
    private String bookCover;   // <-- 추가: Book 엔티티의 bookCover 필드와 일치
    private String userId;
}