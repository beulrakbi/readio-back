package com.team.teamreadioserver.interest.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class InterestUpdateRequestDTO {

    @NotBlank(message = "새 이름은 공백일 수 없습니다.")
    private String newName;
}