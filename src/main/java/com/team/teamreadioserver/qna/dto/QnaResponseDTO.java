package com.team.teamreadioserver.qna.dto;

import com.team.teamreadioserver.qna.entity.Qna;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QnaResponseDTO {
    private Integer qnaId;
    private String qnaTitle;
    private LocalDateTime qnaCreateAt;
    private int qnaView;

    public static QnaResponseDTO fromEntity(Qna qna) {
        QnaResponseDTO qnaResponseDTO = new QnaResponseDTO();
        qnaResponseDTO.qnaId = qna.getQnaId();
        qnaResponseDTO.qnaTitle = qna.getQnaTitle();
        qnaResponseDTO.qnaCreateAt = qna.getQnaCreateAt();
        qnaResponseDTO.qnaView = qna.getQnaView();
        return qnaResponseDTO;
    }
}
