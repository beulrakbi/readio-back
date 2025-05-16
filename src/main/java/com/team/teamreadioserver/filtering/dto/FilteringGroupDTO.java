package com.team.teamreadioserver.filtering.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class FilteringGroupDTO {

    private int groupId;
    private String title;
    private String content;
}
