package com.team.teamreadioserver.filtering.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class FilteringGroupDetailDTO {
    private FilteringGroupDTO filteringGroup;
    private List<FilteringDTO> filterings;
}
