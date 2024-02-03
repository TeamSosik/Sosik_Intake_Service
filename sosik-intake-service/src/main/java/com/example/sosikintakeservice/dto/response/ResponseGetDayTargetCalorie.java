package com.example.sosikintakeservice.dto.response;

import com.example.sosikintakeservice.model.entity.DayTargetCalorieEntity;
import lombok.Builder;

@Builder
public record ResponseGetDayTargetCalorie(Integer dayTargetKcal) {
    public static ResponseGetDayTargetCalorie buildResponseGetDayTargetCalorie(DayTargetCalorieEntity dayTargetCalorieEntity){
        return ResponseGetDayTargetCalorie.builder()
                .dayTargetKcal(dayTargetCalorieEntity.getDayTargetKcal())
                .build();
    }
}
