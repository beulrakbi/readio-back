package com.team.teamreadioserver.notice.controller;

import com.team.teamreadioserver.notice.dto.NoticeRequestDTO;
import com.team.teamreadioserver.notice.dto.NoticeResponseDTO;
import com.team.teamreadioserver.notice.dto.NoticeUpdateDTO;
import com.team.teamreadioserver.notice.entity.Notice;
import com.team.teamreadioserver.notice.repository.NoticeRepository;
import com.team.teamreadioserver.notice.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/serviceCenter")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;
    @Autowired
    private NoticeRepository noticeRepository;

    @Operation(summary = "공지 등록", description = "새로운 공지사항을 등록합니다.")
    @PostMapping(value = "/notice/write", consumes = {"multipart/form-data"}) // consumes 타입 변경
    public String createNotice(@ModelAttribute @Valid NoticeRequestDTO noticeRequestDTO) { // @RequestBody -> @ModelAttribute
        noticeService.writeNotice(noticeRequestDTO);
        return "공지사항이 성공적으로 등록되었습니다.";
    }

    @Operation(summary = "공지사항 수정", description ="공지사항을 수정합니다.")
    @PutMapping(value = "/notice/update", consumes = {"multipart/form-data"}) // consumes 타입 변경
    public String updateNotice(@ModelAttribute @Valid NoticeUpdateDTO noticeUpdateDTO) { // @RequestBody -> @ModelAttribute
        noticeService.updateNotice(noticeUpdateDTO);
        return "공지사항이 성공적으로 수정되었습니다.";
    }

    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.")
    @DeleteMapping("/notice/delete/{noticeId}")
    public String deleteNotice(@PathVariable Integer noticeId) {
        noticeService.deleteNotice(noticeId);
        return "공지사항이 삭제되었습니다.";
    }

    @Operation(summary = "공지사항 목록 조회", description = "공지사항 리스트를 조회합니다.")
    @GetMapping("/notice/list")
    public List<NoticeResponseDTO> noticeList() {
        return noticeService.getNoticeList();
    }

    @Operation(summary = "공지사항 상세", description = "공지사항 게시글을 상세 조회합니다.")
    @GetMapping("/notice/detail/{noticeId}")
    public ResponseEntity<NoticeResponseDTO> getNoticeDetail(@PathVariable Integer noticeId) {
        NoticeResponseDTO notice = noticeService.detail(noticeId);
        return ResponseEntity.ok(notice);
    }
    @Operation(summary = "페이징 처리", description = "게시글 수에 맞춰 페이징 처리합니다.")
    @GetMapping("/notice/list/paging")
    public Page<Notice> list(@PageableDefault(page=0, size = 7, sort = "noticeId", direction = Sort.Direction.DESC) Pageable pageable)  {
        return noticeRepository.findAll(pageable);
    }

    @Operation(summary = "제목으로 공지사항 검색", description = "제목에 포함된 키워드로 공지사항을 검색합니다.")
    @GetMapping("/notice/search")
    public ResponseEntity<List<NoticeResponseDTO>> searchNotices(@RequestParam String keyword) {
        List<NoticeResponseDTO> results = noticeService.searchNoticesByTitle(keyword);
        return ResponseEntity.ok(results);
    }
}