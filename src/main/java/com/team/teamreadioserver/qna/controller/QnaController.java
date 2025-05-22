package com.team.teamreadioserver.qna.controller;

import com.team.teamreadioserver.qna.dto.QnaAnswerDTO;
import com.team.teamreadioserver.qna.dto.QnaQuestionDTO;
import com.team.teamreadioserver.qna.entity.Qna;
import com.team.teamreadioserver.qna.repository.QnaRepository;
import com.team.teamreadioserver.qna.service.QnaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/serviceCenter")
public class QnaController {
    @Autowired
    private QnaService qnaService;
    @Autowired
    private QnaRepository qnaRepository;

    @Operation(summary = "Qna질문 등록", description = "Qna 질문 등록합니다.")
    @PostMapping("/qna/question")
    public String createQna(@RequestBody @Valid QnaQuestionDTO qnaQuestionDTO) {
        qnaService.wirteQna(qnaQuestionDTO);
        return "QNA가 성공적으로 등록되었습니다.";
    }

    @Operation(summary = "Qna답변 등록", description = "Qna 답변을 등록합니다.")
    @PutMapping("/qna/Answer")
    public ResponseEntity<String> writeAnswer(@RequestBody @Valid QnaAnswerDTO qnaAnswerDTO) {
        qnaService.updateQnaAnswer(qnaAnswerDTO);
        return ResponseEntity.ok("답변이 등록되었습니다.");
    }

    @Operation(summary = "Qna답변 삭제", description = "Qna답변을 삭제합니다.")
    @PutMapping("/qna/Answer/delete")
    public ResponseEntity<String> deleteAnswer(@RequestBody @Valid QnaAnswerDTO qnaAnswerDTO) {
        qnaService.updateQnaAnswer(qnaAnswerDTO);
        return ResponseEntity.ok("답변이 삭제되었습니다.");
    }

    @Operation(summary = "페이징 처리", description = "게시글 수에 맞춰 페이징 처리합니다.")
    @GetMapping("/qna/list/paging")
    public Page<Qna> list(@PageableDefault(page=0, size = 7, sort = "qnaId", direction = Sort.Direction.DESC) Pageable pageable){
        return qnaRepository.findAll(pageable);
    }

}
