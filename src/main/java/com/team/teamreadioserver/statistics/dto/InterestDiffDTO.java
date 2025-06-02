package com.team.teamreadioserver.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InterestDiffDTO {
    private String label;
    private Long countMonth1;
    private Long countMonth2;
    private Long diff; // month2 - month1
}


