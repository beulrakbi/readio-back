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
    private String imageUrl; // 이미지 URL 필드 추가

    public static NoticeResponseDTO fromEntity(Notice notice) {
        NoticeResponseDTO dto = new NoticeResponseDTO();
        dto.noticeId = notice.getNoticeId();
        dto.noticeTitle = notice.getNoticeTitle();
        dto.noticeContent = notice.getNoticeContent();
        dto.noticeState = notice.getNoticeState();
        dto.noticeCreateAt = notice.getNoticeCreateAt();
        dto.noticeView = notice.getNoticeView();
        dto.userId = notice.getUserId();
        // 이미지 정보가 있을 경우 imageUrl 설정 (실제 URL은 서비스에서 세팅)
        return dto;
    }
}