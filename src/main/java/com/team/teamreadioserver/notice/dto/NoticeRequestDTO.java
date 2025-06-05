package com.team.teamreadioserver.notice.dto;

import com.team.teamreadioserver.notice.entity.NoticeImg;
import com.team.teamreadioserver.notice.enumPackage.NoticeState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NoticeRequestDTO {
    private Integer noticeId;

    @NotBlank(message = "제목은 공백이 아니어야 합니다.")
    private String noticeTitle;

    @NotBlank(message = "내용은 공백이 아니어야 합니다.")
    private String noticeContent;

    @NotNull(message = "말머리는 무조건 선택되어야 합니다.")
    private NoticeState noticeState;

    private NoticeImgDTO noticeImg;
}