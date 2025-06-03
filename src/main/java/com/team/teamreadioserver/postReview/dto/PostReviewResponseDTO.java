package com.team.teamreadioserver.postReview.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.teamreadioserver.post.entity.Post;
import com.team.teamreadioserver.profile.dto.ProfileResponseDTO;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private Date postReviewDate;
    private ProfileResponseDTO profileId;
    private Post postId;
    private int postReviewLike;
}
