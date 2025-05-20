package com.team.teamreadioserver.notice.controller;

import com.team.teamreadioserver.notice.dto.NoticeRequestDTO;
import com.team.teamreadioserver.notice.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/board")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;
    @Operation(summary = "공지 등록", description = "새로운 공지사항을 등록합니다.")
    @PostMapping("/notice")
    public String createNotice(@RequestBody @Valid NoticeRequestDTO noticeRequestDTO) {
        noticeService.writeNotice(noticeRequestDTO);
        return "공지사항이 성공적으로 등록되었습니다.";
    }

    @GetMapping("/test")
    public String test() {
        return "Hello Swagger!";
    }
}
