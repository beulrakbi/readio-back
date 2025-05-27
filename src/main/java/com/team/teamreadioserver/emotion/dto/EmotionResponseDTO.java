package com.team.teamreadioserver.emotion.dto;

import com.team.teamreadioserver.emotion.entity.Emotion;
import com.team.teamreadioserver.emotion.enums.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionResponseDTO {

    private String emotionCode;
    private String emotionLabel;
    private LocalDate date;

    public static EmotionResponseDTO fromEntity(Emotion emotion) {
        EmotionType type = emotion.getEmotionType();
        return new EmotionResponseDTO(
                type.name(),          // code
                type.getLabel(),      // label
                emotion.getCreatedAt().toLocalDate()
        );
    }
}
