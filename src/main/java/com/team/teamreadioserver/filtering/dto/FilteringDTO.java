package com.team.teamreadioserver.filtering.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class FilteringDTO {
    private int filteringId;
    private int groupId;
    private String videoId;
    private String keyword;

}
