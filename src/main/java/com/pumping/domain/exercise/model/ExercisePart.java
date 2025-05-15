package com.pumping.domain.exercise.model;

public enum ExercisePart {
    CHEST("가슴"),
    BACK("등"),
    SHOULDERS("어깨"),
    ARMS("팔"),
    CORE("코어"),
    LEGS("하체"),
    HIP("엉덩이");

    private final String koreanName;

    ExercisePart(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    @Override
    public String toString() {
        return koreanName;
    }
}