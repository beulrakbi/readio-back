// 변경 제안 (사소한 부분 포함)
package com.team.teamreadioserver.bookReview.dto;

import com.team.teamreadioserver.bookReview.enumPackage.IsHidden;
import lombok.*;
import java.time.LocalDateTime; // LocalDateTime import
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor // @RequiredArgsConstructor 삭제 고려
@NoArgsConstructor // @NoArgsConstructor 추가
@Builder // Builder 패턴 사용 시
public class BookReviewDTO {
    private Integer reviewId;
    private Long profileId; // Long 타입으로 변경
    private String penName;
    private String bookIsbn;
    private String reviewContent;
    private Integer reportedCount;
    private IsHidden isHidden;
    private Date createdAt; // LocalDateTime으로 변경
    private String reviewerUserId;

    private boolean isLiked;    // 현재 사용자가 이 리뷰에 좋아요를 눌렀는지 여부
    private Integer likesCount;
}
