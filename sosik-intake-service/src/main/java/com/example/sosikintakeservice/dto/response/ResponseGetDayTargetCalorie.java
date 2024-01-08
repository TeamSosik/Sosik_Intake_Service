package com.example.sosikintakeservice.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ResponseGetDayTargetCalorie(
        Long memberId,
        Integer dayTargetKcal,
        Integer dailyIntakePurpose
) {
}
