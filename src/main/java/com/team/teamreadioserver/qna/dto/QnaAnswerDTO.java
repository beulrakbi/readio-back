package com.team.teamreadioserver.qna.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QnaAnswerDTO {
    private Integer qnaId;
    @NotBlank(message = "답변은 공백이 아니어야 합니다.")
    private String qnaAnswer;
}
