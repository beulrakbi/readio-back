package com.team.teamreadioserver.qna.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QnaQuestionDTO {
    private Integer qnaId;
    @NotBlank(message = "질문 제목은 공백이 아니어야 합니다.")
    private String qnaTitle;
    @NotBlank(message = "질문 내용은 공백이 아니어야 합니다.")
    private String qnaQuestion;
}
