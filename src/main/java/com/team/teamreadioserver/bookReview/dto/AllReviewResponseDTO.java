package com.team.teamreadioserver.bookReview.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllReviewResponseDTO {
    private Long profileId;
    private String reviewContent;
    private Date createdAt;
}
