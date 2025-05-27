package com.team.teamreadioserver.qna.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class QnaAnswerDTO {
    private Integer qnaId;
    private String qnaAnswer;
}
