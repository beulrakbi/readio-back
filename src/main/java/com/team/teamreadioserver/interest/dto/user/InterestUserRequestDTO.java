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

    @Size(max = 3, message = "관심분야는 최대 3개까지 선택가능합니다.")
    private List<Long> categoryIds;

    @Size(max = 5, message = "관심키워드는 최대 5개까지 선택가능합니다.")
    private List<Long> keywordIds;
}
