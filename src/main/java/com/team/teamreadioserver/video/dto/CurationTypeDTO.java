package com.team.teamreadioserver.video.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class CurationTypeDTO {
    private int typeId;
    private String typeName;
    private String typeText;
}
