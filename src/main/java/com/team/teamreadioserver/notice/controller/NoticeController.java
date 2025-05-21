package com.team.teamreadioserver.notice.controller;

import com.team.teamreadioserver.notice.dto.NoticeRequestDTO;
import com.team.teamreadioserver.notice.dto.NoticeResponseDTO;
import com.team.teamreadioserver.notice.dto.NoticeUpdateDTO;
import com.team.teamreadioserver.notice.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/serviceCenter")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Operation(summary = "공지 등록", description = "새로운 공지사항을 등록합니다.")
    @PostMapping("/notice/write")
    public String createNotice(@RequestBody @Valid NoticeRequestDTO noticeRequestDTO) {
        noticeService.writeNotice(noticeRequestDTO);
        return "공지사항이 성공적으로 등록되었습니다.";
    }

    @Operation(summary = "공지사항 수정", description ="공지사항을 수정합니다.")
    @PutMapping("/notice/update")
    public String updateNotice(@RequestBody @Valid NoticeUpdateDTO noticeUpdateDTO) {
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

}
