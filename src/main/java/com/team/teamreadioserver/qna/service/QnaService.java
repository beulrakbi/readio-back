package com.team.teamreadioserver.qna.service;

import com.team.teamreadioserver.notice.dto.NoticeResponseDTO;
import com.team.teamreadioserver.qna.dto.QnaAnswerDTO;
import com.team.teamreadioserver.qna.dto.QnaDetailDTO;
import com.team.teamreadioserver.qna.dto.QnaQuestionDTO;
import com.team.teamreadioserver.qna.dto.QnaResponseDTO;
import com.team.teamreadioserver.qna.entity.Qna;
import com.team.teamreadioserver.qna.repository.QnaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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


    public List<QnaResponseDTO> getQnaList(){
        List<Qna> qnas = qnaRepository.findAllByOrderByQnaCreateAtDesc();

        return qnas.stream()
                .map(qnas1 -> new QnaResponseDTO(
                        qnas1.getQnaId(),
                        qnas1.getQnaTitle(),
                        qnas1.getQnaCreateAt(),
                        qnas1.getQnaView()
                ))
                .collect(Collectors.toList());
    }

    // 질문 상세 조회
    public QnaDetailDTO getQnaDetail(Integer qnaId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("해당 QNA가 없습니다."));

        return new QnaDetailDTO(
                qna.getQnaId(),
                qna.getQnaTitle(),
                qna.getQnaQuestion(),
                qna.getQnaAnswer(),         // ✨ 답변도 함께 반환
                qna.getQnaCreateAt()
        );
    }

    public List<QnaResponseDTO> searchQnaByTitle(String qnaTitle) {
        List<Qna> qna = qnaRepository.findByQnaTitleContainingIgnoreCase(qnaTitle);

        return qna.stream()
                .map(QnaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<QnaResponseDTO> getMyQnaList() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Qna> myQnaList = qnaRepository.findByUserId(userId);
        return myQnaList.stream()
                .map(QnaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
