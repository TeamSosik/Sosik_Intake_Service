package com.example.sosikintakeservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateTargetCalorie(Long Id,
                                  @NotNull
                                  @Size(min = 1,max = 5 ,message = "목표 칼로리는 5자리를 넘지 않게 입력해주세요")
                                  Integer dayTargetKcal,
                                  @NotNull
                                  Integer dailyIntakePurpose) {

}
