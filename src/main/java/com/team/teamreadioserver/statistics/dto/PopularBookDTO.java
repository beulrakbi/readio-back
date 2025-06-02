package com.team.teamreadioserver.statistics.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PopularBookDTO {
    private String bookIsbn;
    private String bookTitle;
    private String bookAuthor;
    private String bookCover;
    private Long clickCount;
}
