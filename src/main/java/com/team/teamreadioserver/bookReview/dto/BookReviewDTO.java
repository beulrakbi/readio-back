package com.team.teamreadioserver.bookReview.dto;

import com.team.teamreadioserver.bookReview.enumPackage.IsHidden;
import lombok.*;
import java.time.LocalDateTime; // LocalDateTime import
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

    private boolean isLiked;
    private Integer likesCount;
}