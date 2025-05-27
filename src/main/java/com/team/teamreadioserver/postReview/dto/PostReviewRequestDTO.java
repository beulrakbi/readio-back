package com.team.teamreadioserver.postReview.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewRequestDTO {
    private int postReviewId;
    private String postReivewContent;
}
