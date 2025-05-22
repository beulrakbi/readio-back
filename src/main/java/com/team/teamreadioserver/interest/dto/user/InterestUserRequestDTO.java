package com.team.teamreadioserver.interest.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InterestUserRequestDTO {

    @NotNull
    private String userId;

    @Size(max = 3)
    private List<Long> categoryIds;

    @Size(max = 5)
    private List<Long> keywordIds;
}
