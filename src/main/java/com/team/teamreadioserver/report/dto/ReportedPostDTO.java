package com.team.teamreadioserver.report.dto;

import com.team.teamreadioserver.bookReview.enumPackage.IsHidden;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ReportedPostDTO {
    private int reportId;
    private int postId;
    private String userId;
    private Date reportedDate;

    private String bookIsbn;
    private String postTitle;
    private String postContent;
    private Date postCreatedAt;
    private int reportedCount;
    private String isHidden;

}
