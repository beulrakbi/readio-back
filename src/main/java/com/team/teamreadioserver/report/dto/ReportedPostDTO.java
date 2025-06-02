package com.team.teamreadioserver.report.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ReportedPostDTO {
    private int reportId;
    private int postId;
    private String userId;
    private Date reportedDate;

}
