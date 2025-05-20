package com.team.teamreadioserver.notice.controller;

import com.team.teamreadioserver.notice.dto.NoticeRequestDTO;
import com.team.teamreadioserver.notice.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping("/notice")
    public String createNotice(@RequestBody @Valid NoticeRequestDTO noticeRequestDTO) {
        noticeService.writeNotice(noticeRequestDTO);
        return "공지사항이 성공적으로 등록되었습니다.";
    }
}
