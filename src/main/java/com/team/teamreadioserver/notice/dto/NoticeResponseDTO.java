package com.team.teamreadioserver.notice.dto;

import com.team.teamreadioserver.notice.entity.Notice;
import com.team.teamreadioserver.notice.enumPackage.NoticeState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponseDTO {
    private Integer noticeId;
    private String noticeTitle;
    private String noticeContent;
    private LocalDateTime noticeCreateAt;
    private int noticeView;
    private NoticeState noticeState;
    private String userId;

    public static NoticeResponseDTO fromEntity(Notice notice) {
        NoticeResponseDTO dto = new NoticeResponseDTO();
        dto.noticeId = notice.getNoticeId();
        dto.noticeTitle = notice.getNoticeTitle();
        dto.noticeContent = notice.getNoticeContent();
        dto.noticeState = notice.getNoticeState();
        dto.noticeCreateAt = notice.getNoticeCreateAt(); // 날짜 형식에 따라 변경 가능
        dto.noticeView = notice.getNoticeView();
        dto.userId = notice.getUserId();
        return dto;
    }
}