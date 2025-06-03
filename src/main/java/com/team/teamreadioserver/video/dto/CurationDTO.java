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
    private List<Object> otherKeywords;

    public CurationDTO(CurationTypeDTO type, List<CurationKeywordsDTO> keywords) {
        this.curationType = type;
        this.curationKeywords = keywords;
    }

    public CurationDTO otherKeywords(CurationTypeDTO type, List<Object> otherKeywords)
    {
        this.curationType = type;
        this.otherKeywords = otherKeywords;
        return this;
    }

}
