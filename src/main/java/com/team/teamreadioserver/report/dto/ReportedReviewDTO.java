package com.team.teamreadioserver.report.dto;

import com.team.teamreadioserver.bookReview.enumPackage.IsHidden;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ReportedReviewDTO {
    private int reportId;
    private int reviewId;
    private String userId;
    private Date reportedDate;

    private String bookIsbn;
    private String reviewContent;
    private Date createdAt;
    private int reportedCount;
    private IsHidden isHidden;

}
