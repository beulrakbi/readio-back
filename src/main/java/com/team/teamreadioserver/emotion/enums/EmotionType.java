package com.team.teamreadioserver.emotion.enums;

import lombok.Getter;

@Getter
public enum EmotionType {
    NORMAL("보통"),
    HAPPY("기쁨"),
    SAD("슬픔"),
    ANGRY("화남"),
    ANXIOUS("불안");

    private final String label;

    EmotionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
