package com.team.teamreadioserver.bookReview.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // Jackson 어노테이션 import
import com.team.teamreadioserver.bookReview.enumPackage.IsHidden;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookReviewDTO {
    private Integer reviewId;
    private Long profileId;
    private String penName;
    private String bookIsbn;
    private String reviewContent;
    private Integer reportedCount;
    private IsHidden isHidden;
    private Date createdAt;
    private String reviewerUserId;

    @JsonProperty("isLiked") // JSON으로 변환될 때 필드명을 "isLiked"로 명시
    private boolean isLiked;  // getter는 isLiked(), setter는 setLiked()가 생성됨 (Lombok)
    private Integer likesCount;
}