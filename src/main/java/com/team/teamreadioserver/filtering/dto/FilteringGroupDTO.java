package com.team.teamreadioserver.filtering.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class FilteringGroupDTO {

    private int groupId;
    private String title;
    private String content;
    private Date createAt;

}
