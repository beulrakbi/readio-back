package com.team.teamreadioserver.interest.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List
        ;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//사용자가 보내는 데이터 (입력값 검증용)
public class InterestAdminRequestDTO {

    @NotEmpty(message = "카테고리는 최소 1개 이상 등록되어 있어야 합니다.")
    private List<String> categories;

    @NotEmpty(message = "키워드는 최소 1개 이상 등록되어 있어야 합니다.")
    private List<String> keywords;

}
