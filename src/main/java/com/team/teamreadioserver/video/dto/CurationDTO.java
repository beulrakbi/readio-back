package com.team.teamreadioserver.video.dto;

import lombok.*;

import java.util.List;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class CurationDTO {
    private CurationTypeDTO curationType;
    private List<CurationKeywordsDTO> curationKeywords;
}
