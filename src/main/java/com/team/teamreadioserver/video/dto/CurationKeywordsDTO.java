package com.team.teamreadioserver.video.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class CurationKeywordsDTO {
    private int curationId;
    private String keyword;
    private String type;
}
