package com.team.teamreadioserver;

import com.team.teamreadioserver.notice.dto.NoticeRequestDTO;
import com.team.teamreadioserver.notice.enumPackage.NoticeState;
import com.team.teamreadioserver.notice.service.NoticeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TeamReadioServerApplicationTests {
    @Autowired
    private NoticeService noticeService;

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
}
