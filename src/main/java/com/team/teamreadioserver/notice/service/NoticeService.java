package com.team.teamreadioserver.notice.service;

import com.team.teamreadioserver.notice.dto.NoticeRequestDTO;
import com.team.teamreadioserver.notice.entity.Notice;
import com.team.teamreadioserver.notice.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    public void writeNotice(NoticeRequestDTO requestDTO) {
        Notice notice = Notice.builder()
                .noticeTitle(requestDTO.getNoticeTitle())
                .noticeContent(requestDTO.getNoticeContent())
                .noticeState(requestDTO.getNoticeState())
                .noticeImg(requestDTO.getNoticeImg())
                .build();

        noticeRepository.save(notice);
    }
}
