package com.team.teamreadioserver.bookReview.dto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookDetailsDTO {
    private String title;
    private String author;
    private String coverImageUrl;
    // 필요하다면 여기에 ISBN도 포함할 수 있습니다.
    // private String isbn;
}