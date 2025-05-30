package com.team.teamreadioserver.bookReview.dto;

import com.team.teamreadioserver.bookReview.enumPackage.IsHidden;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class BookReviewDTO {
    private Integer reviewId;
    private Integer profileId;
    private String bookIsbn;
    private String reviewContent;
    private Integer reportedCount;
    private IsHidden isHidden;
    private Date createdAt;
}
