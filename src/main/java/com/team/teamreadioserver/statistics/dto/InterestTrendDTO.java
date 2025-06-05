package com.team.teamreadioserver.statistics.dto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class InterestTrendDTO {

    private String period;
    private String label; // 키워드나 카테고리
    private Long count; //선택수
}
