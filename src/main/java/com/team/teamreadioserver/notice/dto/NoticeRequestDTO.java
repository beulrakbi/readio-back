package com.team.teamreadioserver.notice.dto;

import com.team.teamreadioserver.notice.enumPackage.NoticeState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile; // 이 부분을 추가

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeRequestDTO {
    private String noticeTitle;
    private String noticeContent;
    private NoticeState noticeState;
    private MultipartFile noticeImgFile; // 파일 자체를 받기 위한 필드 추가
}