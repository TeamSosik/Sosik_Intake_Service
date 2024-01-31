package com.example.sosikintakeservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record IntakeRankCondition(@NotBlank(message = "값이 비어있을 수 없습니다.")
                                  String rankType
) {

}
