package com.team.teamreadioserver.qna.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QnaDetailDTO {
    private Integer qnaId;
    private String qnaTitle;
    private String qnaQuestion;
    private String qnaAnswer;
    private LocalDateTime qnaCreateAt;
    private int qnaView;
    private String userId; // 이 줄을 추가합니다.
}