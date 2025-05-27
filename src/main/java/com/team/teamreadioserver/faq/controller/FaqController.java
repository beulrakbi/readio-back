package com.team.teamreadioserver.faq.controller;

import com.team.teamreadioserver.faq.dto.FaqCreateDTO;
import com.team.teamreadioserver.faq.dto.FaqResponseDTO;
import com.team.teamreadioserver.faq.dto.FaqUpdateDTO;
import com.team.teamreadioserver.faq.entity.Faq;
import com.team.teamreadioserver.faq.repository.FaqRepository;
import com.team.teamreadioserver.faq.service.FaqService;
import com.team.teamreadioserver.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/serviceCenter")
public class FaqController {
    @Autowired
    private FaqService faqService;
    @Autowired
    private FaqRepository faqRepository;


    @Operation(summary = "페이징 처리", description = "게시글 수에 맞춰 페이징 처리합니다.")
    @GetMapping("/faq/list/paging")
    public Page<Faq> list(@PageableDefault(page=0, size = 7, sort = "faqId", direction = Sort.Direction.DESC)Pageable pageable){
        return faqRepository.findAll(pageable);
    }

    @Operation(summary = "FAQ 등록", description = "새로운 FAQ 등록합니다.")
    @PostMapping("/faq/write")
    public String createFaq(@RequestBody @Valid FaqCreateDTO faqCreateDTO) {
        faqService.writeFaq(faqCreateDTO);
        return "FAQ가 성공적으로 등록되었습니다.";
    }

    @Operation(summary = "FAQ 목록 조회", description = "FAQ 리스트를 조회합니다." )
    @GetMapping("/faq/list")
    public List<FaqResponseDTO> faqList() {
        return faqService.getFaqList();
    }

    @Operation(summary = "FAQ 삭제", description = "FAQ 삭제")
    @DeleteMapping("/faq/delete/{faqId}")
    public String deleteFaq(@PathVariable Integer faqId) {
        faqService.deleteFaq(faqId);
        return "FAQ가 삭제되었습니다.";
    }

    @Operation(summary = "제목으로 FAQ 검색", description = "제목에 포함된 키워드로 FAQ를 검색한다.")
    @GetMapping("/faq/search")
    public ResponseEntity<List<FaqResponseDTO>> searchFaq(@RequestParam String keyword) {
        List<FaqResponseDTO> results = faqService.searchFaqByTitle(keyword);
        return ResponseEntity.ok().body(results);
    }


    @Operation(summary = "FAQ 수정", description = "FAQ 게시글을 수정합니다.")
    @PutMapping("/faq/update")
    public ResponseEntity<String> updateFaq(@RequestBody @Valid FaqUpdateDTO faqUpdateDTO) {
        faqService.updateFaq(faqUpdateDTO);
        return ResponseEntity.ok("FAQ가 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "FAQ 상세 조회", description = "FAQ 게시글을 상세 조회합니다.")
    @GetMapping("/faq/detail/{faqId}")
    public ResponseEntity<FaqUpdateDTO> getFaq(@PathVariable Integer faqId) {
        FaqUpdateDTO faq = faqService.detail(faqId);
        return ResponseEntity.ok(faq);
    }
}
