package com.team.teamreadioserver.notice.service;

import com.team.teamreadioserver.notice.dto.NoticeRequestDTO;
import com.team.teamreadioserver.notice.dto.NoticeUpdateDTO;
import com.team.teamreadioserver.notice.entity.Notice;
import com.team.teamreadioserver.notice.repository.NoticeRepository;
import jakarta.transaction.Transactional;
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
    @Transactional
    public void updateNotice(NoticeUpdateDTO updateDTO) {
        Notice notice = noticeRepository.findById(updateDTO.getNoticeId())
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 없습니다."));

        notice.update(
                updateDTO.getNoticeTitle(),
                updateDTO.getNoticeContent(),
                updateDTO.getNoticeState(),
                updateDTO.getNoticeImg()
        );
    }
}
