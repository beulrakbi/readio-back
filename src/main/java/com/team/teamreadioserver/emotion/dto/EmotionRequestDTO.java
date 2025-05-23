package com.team.teamreadioserver.emotion.dto;

import com.team.teamreadioserver.emotion.enums.EmotionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmotionRequestDTO {

    @NotNull(message = "userId는 필수입니다.")
    private String userId;

    private EmotionType emotionType;

    private LocalDate date;
}