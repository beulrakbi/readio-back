package com.team.teamreadioserver.postReview.dto;

import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.profile.entity.Profile;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewResponseDTO {
    private int postReviewId;
    private String postReviewContent;
    private String postReviewDate;
    private Profile profileId;
    private Post postId;
    private int postReviewLike;
}
