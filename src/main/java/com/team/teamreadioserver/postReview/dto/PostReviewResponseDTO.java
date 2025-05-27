package com.team.teamreadioserver.postReview.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewResponseDTO {
    private int postReviewId;
    private String postReivewContent;
    private Date postReviewDate;
    private int profileId;
    private int postId;
    private int postReviewLike;
}
