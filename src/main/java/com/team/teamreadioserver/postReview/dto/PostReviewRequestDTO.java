package com.team.teamreadioserver.postReview.dto;

import com.team.teamreadioserver.profile.entity.Profile;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewRequestDTO {
    private int postReviewId;
    private String postReviewContent;
    private Integer profileId;
}
