// src/main/java/com/team/teamreadioserver/bookmark/dto/BookBookmarkRequestDTO.java
package com.team.teamreadioserver.bookmark.dto;

import jakarta.validation.constraints.NotBlank; // 유효성 검사를 위해 @NotBlank 임포트
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookBookmarkRequestDTO {
    @NotBlank(message = "책 ISBN은 필수입니다.") // 클라이언트가 bookIsbn을 반드시 보내도록 강제
    private String bookIsbn;
    // userId는 @AuthenticationPrincipal UserDetails에서 가져올 것이므로 여기에 포함하지 않습니다.
}