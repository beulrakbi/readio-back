package com.team.teamreadioserver.notice.dto;

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
}
