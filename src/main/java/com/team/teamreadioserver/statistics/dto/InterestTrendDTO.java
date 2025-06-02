package com.team.teamreadioserver.statistics.dto;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class InterestTrendDTO {

    private String period;
    private String label; // 키워드나 카테고리
    private Long count; //선택수
}
