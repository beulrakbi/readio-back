package com.team.teamreadioserver.clicklog.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ClickLogDTO {
    private String userId;
    private String contentType;
    private String contentId;

}
