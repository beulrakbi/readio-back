package com.team.teamreadioserver.bookReview.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyReviewResponseDTO {
    private String reviewContent;
    private Date createdAt;
}
