// 변경 제안
package com.team.teamreadioserver.bookReview.dto;

import com.team.teamreadioserver.bookReview.dto.BookDetailsDTO;
import com.team.teamreadioserver.search.dto.BookDTO;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyReviewResponseDTO {
    private Integer reviewId;
    private String bookIsbn;
    private String reviewContent;
    private Date createdAt;
    private BookDTO book; // 책 상세 정보를 담을 필드 추가
}