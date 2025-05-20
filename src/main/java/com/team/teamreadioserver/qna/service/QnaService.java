package com.team.teamreadioserver.qna.service;

import com.team.teamreadioserver.qna.dto.QnaAnswerDTO;
import com.team.teamreadioserver.qna.dto.QnaQuestionDTO;
import com.team.teamreadioserver.qna.entity.Qna;
import com.team.teamreadioserver.qna.repository.QnaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QnaService {
    @Autowired
    private QnaRepository qnaRepository;
    //질문 등록
    public void wirteQna(QnaQuestionDTO qnaQuestionDTO) {
        Qna qna = Qna.builder()
                .qnaTitle(qnaQuestionDTO.getQnaTitle())
                .qnaQuestion(qnaQuestionDTO.getQnaQuestion())
                .build();
        qnaRepository.save(qna);
    }
    
    //질문 수정
    @Transactional
    public void updateQna(QnaQuestionDTO qnaQuestionDTO) {
        Qna qna = qnaRepository.findById(qnaQuestionDTO.getQnaId())
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다."));
        
        qna.updateQuestion(
                qnaQuestionDTO.getQnaTitle(),
                qnaQuestionDTO.getQnaQuestion()
        );
    }
    //질문 삭제
    @Transactional
    public void deleteQna(Integer qnaId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(()-> new IllegalArgumentException("해당 질문이 없습니다."));

        qnaRepository.delete(qna);
    }

    //답변 등록 / 수정 / 삭제
    @Transactional
    public void updateQnaAnswer(QnaAnswerDTO qnaAnswerDTO) {
        Qna qna = qnaRepository.findById(qnaAnswerDTO.getQnaId())
                .orElseThrow(()-> new IllegalArgumentException("해당 질문이 없습니다."));
        qna.createAnswer(
                qnaAnswerDTO.getQnaAnswer()
        );
    }
}
