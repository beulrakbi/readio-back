package com.team.teamreadioserver.postReview.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewRequestDTO {
    private int postReviewId;
    private String postReivewContent;
    private String postReviewDate;
    private int profileId;
}
