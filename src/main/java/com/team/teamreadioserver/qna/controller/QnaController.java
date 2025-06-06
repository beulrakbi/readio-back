package com.team.teamreadioserver.qna.controller;

import com.team.teamreadioserver.qna.dto.QnaAnswerDTO;
import com.team.teamreadioserver.qna.dto.QnaDetailDTO;
import com.team.teamreadioserver.qna.dto.QnaQuestionDTO;
import com.team.teamreadioserver.qna.dto.QnaResponseDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        qnaService.deleteQnaAnswer(qnaAnswerDTO); // ✨ deleteAnswer 메서드 호출하도록 수정
        return ResponseEntity.ok("답변이 삭제되었습니다.");
    }

    @Operation(summary = "페이징 처리", description = "게시글 수에 맞춰 페이징 처리합니다.")
    @GetMapping("/qna/list/paging")
    public Page<Qna> list(@PageableDefault(page=0, size = 7, sort = "qnaId", direction = Sort.Direction.DESC) Pageable pageable){
        return qnaRepository.findAll(pageable);
    }

    @Operation(summary = "Qna 리스트 조회", description = "Qna 리스트를 조회합니다.")
    @GetMapping("/qna/list")
    public List<QnaResponseDTO>  qnaList(){
        return qnaService.getQnaList();
    }

    @Operation(summary = "Qna 질문 삭제", description = "Qna 질문 삭제")
    @DeleteMapping("/qna/delete/{qnaId}")
    public ResponseEntity<String> deleteQna(@PathVariable Integer qnaId) {
        try {
            qnaService.deleteQna(qnaId);
            return ResponseEntity.ok("삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패: " + e.getMessage());
        }
    }

    @Operation(summary = "qna상세페이지 조회", description = "qna 상세페이지가 조회됩니다.")
    @GetMapping("/qna/detail/{qnaId}")
    public ResponseEntity<QnaDetailDTO> getQnaDetail(@PathVariable Integer qnaId) {
        QnaDetailDTO detailDTO = qnaService.getQnaDetail(qnaId); // ✨ 여기서 조회수가 증가됩니다.
        return ResponseEntity.ok(detailDTO);
    }

    @Operation(summary = "제목으로 Q&A 검색", description = "제목에 포함된 키워드로 Q&A를 검삭핸다")
    @GetMapping("/qna/search")
    public ResponseEntity<List<QnaResponseDTO>> searchQna(@RequestParam String keyword) {
        List<QnaResponseDTO> result = qnaService.searchQnaByTitle(keyword);
        return ResponseEntity.ok().body(result);
    }

    @Operation(summary = "Qna 질문 수정", description = "Qna 질문을 수정합니다.")
    @PutMapping("/qna/update")
    public ResponseEntity<String> updateQna(@RequestBody @Valid QnaQuestionDTO qnaQuestionDTO) {
        try {
            qnaService.updateQna(qnaQuestionDTO);
            return ResponseEntity.ok("QNA가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패: " + e.getMessage());
        }
    }

    @Operation(summary = "답변이 없는 Q&A 개수 조회", description = "답변이 아직 등록되지 않은 Q&A 게시글의 개수를 조회합니다.")
    @GetMapping("/admin/qna/unanswered-count") // 프론트엔드에서 요청할 URL
//    @PreAuthorize("hasRole('ROLE_ADMIN')") // 관리자만 접근 가능하도록 설정 (보안 설정에 따라 변경될 수 있음)
    public ResponseEntity<Map<String, Long>> getUnansweredQnaCount() {
        try {
            long count = qnaService.getUnansweredQnaCount();
            // JSON 응답 형식: {"unansweredCount": N}
            return ResponseEntity.ok(Collections.singletonMap("unansweredCount", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", -1L)); // 에러 발생 시 -1 또는 다른 적절한 값 반환
        }
    }
}

//풀리퀘스트용 주석