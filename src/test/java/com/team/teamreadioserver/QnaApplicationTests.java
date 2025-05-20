package com.team.teamreadioserver;

import com.team.teamreadioserver.faq.dto.FaqCreateDTO;
import com.team.teamreadioserver.faq.dto.FaqUpdateDTO;
import com.team.teamreadioserver.faq.entity.Faq;
import com.team.teamreadioserver.faq.repository.FaqRepository;
import com.team.teamreadioserver.faq.service.FaqService;
import com.team.teamreadioserver.qna.dto.QnaAnswerDTO;
import com.team.teamreadioserver.qna.dto.QnaQuestionDTO;
import com.team.teamreadioserver.qna.entity.Qna;
import com.team.teamreadioserver.qna.repository.QnaRepository;
import com.team.teamreadioserver.qna.service.QnaService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class QnaApplicationTests {
    @Autowired
    private QnaService qnaService;
    @Autowired
    private QnaRepository qnaRepository;
    //질문 등록
    @Test
    void contextLoads() {
        QnaQuestionDTO questionDTO = new QnaQuestionDTO(
                null,
                "테스트 제목",
                "테스트 내용"
        );

        qnaService.wirteQna(questionDTO);
    }
    //수정하기
    @Test
    void testUpdateQna() {
        List<Qna> qnas = qnaRepository.findAll();
        assertFalse(qnas.isEmpty());
        Qna savedQna = qnas.get(0);

        QnaQuestionDTO questionDTO = new QnaQuestionDTO(
                savedQna.getQnaId(),
                "Qna 제목 수정 테스트",
                "Qna 내용 수정 테스트"
        );
        qnaService.updateQna(questionDTO);
        Qna updatedQna = qnaRepository.findById(savedQna.getQnaId()).get();
        assertEquals("Qna 제목 수정 테스트", updatedQna.getQnaTitle());
        assertEquals("Qna 내용 수정 테스트", updatedQna.getQnaQuestion());
    }
    //삭제하기
    @Test
    void testDeleteQna(){
        List<Qna> qnas = qnaRepository.findAll();
        assertFalse(qnas.isEmpty());
        Qna savedQna = qnas.get(qnas.size()-1);

        qnaService.deleteQna(savedQna.getQnaId());
        boolean exists = qnaRepository.findById(savedQna.getQnaId()).isPresent();
        assertFalse(exists);
    }

    //답변 등록
    @Test
    void testCreateAnswer(){
        List<Qna> qnas = qnaRepository.findAll();
        assertFalse(qnas.isEmpty());
        Qna savedQna = qnas.get(0);
        QnaAnswerDTO answerDTO = new QnaAnswerDTO(
                savedQna.getQnaId(),
                "답변 테스트중입니다."
        );
        qnaService.updateQnaAnswer(answerDTO);
        Qna updatedQna = qnaRepository.findById(savedQna.getQnaId()).get();
        assertEquals("답변 테스트중입니다.", updatedQna.getQnaAnswer());
    }
}
