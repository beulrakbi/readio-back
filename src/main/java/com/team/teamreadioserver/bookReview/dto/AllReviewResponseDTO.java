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
public class AllReviewResponseDTO {
    private Integer reviewId; // 리뷰 ID 추가
    private String penName;   // 프로필명 추가 (profileId 대신 또는 함께)
    // private Long profileId; // 필요하다면 profileId도 포함 가능
    private String reviewContent;
    private Date createdAt; // LocalDateTime으로 변경
}