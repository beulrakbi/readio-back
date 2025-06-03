package com.team.teamreadioserver.statistics.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBehaviorLogDTO {
    private String userId;
    private String section;     // myLibrary, video, post
    private Long stayTime;
    private Integer clickCount;
    private LocalDate logDate;
}
