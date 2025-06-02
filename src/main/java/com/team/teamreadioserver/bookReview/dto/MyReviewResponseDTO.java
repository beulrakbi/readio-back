// 변경 제안
package com.team.teamreadioserver.bookReview.dto;

import lombok.*;
import java.time.LocalDateTime; // LocalDateTime import
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyReviewResponseDTO {
    private Integer reviewId; // 리뷰 ID 추가
    private String bookIsbn; // 책 ISBN 추가
    private String reviewContent;
    private Date createdAt; // LocalDateTime으로 변경
}