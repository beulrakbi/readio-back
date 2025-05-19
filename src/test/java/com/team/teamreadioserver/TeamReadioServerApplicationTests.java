package com.team.teamreadioserver;

import com.team.teamreadioserver.notice.dto.NoticeRequestDTO;
import com.team.teamreadioserver.notice.dto.NoticeUpdateDTO;
import com.team.teamreadioserver.notice.entity.Notice;
import com.team.teamreadioserver.notice.enumPackage.NoticeState;
import com.team.teamreadioserver.notice.repository.NoticeRepository;
import com.team.teamreadioserver.notice.service.NoticeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class TeamReadioServerApplicationTests {
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private NoticeRepository noticeRepository;

    @Test
    void contextLoads() {
        NoticeRequestDTO dto = new NoticeRequestDTO(
                null,
                "테스트 제목",
                "테스트 내용",
                NoticeState.TEMPORARY,
                null
        );

        noticeService.writeNotice(dto);
    }

    @Test
    void testUpdateNotice() {
        List<Notice> notices = noticeRepository.findAll();
        assertFalse(notices.isEmpty());
        Notice savedNotice = notices.get(4);

        NoticeUpdateDTO updateDto = new NoticeUpdateDTO(
                savedNotice.getNoticeId(),
                "게시글 수정 테스트",
                "게시글 수정 테스트",
                NoticeState.URGENT,
                null // NoticeImg 인자 추가
        );


        noticeService.updateNotice(updateDto);

        Notice updatedNotice = noticeRepository.findById(savedNotice.getNoticeId()).get();
        assertEquals("게시글 수정 테스트", updatedNotice.getNoticeTitle());
        assertEquals("게시글 수정 테스트", updatedNotice.getNoticeContent());
        assertEquals(NoticeState.URGENT, updatedNotice.getNoticeState());
    }
}
