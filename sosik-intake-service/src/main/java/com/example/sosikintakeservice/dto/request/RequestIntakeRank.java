package com.example.sosikintakeservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RequestIntakeRank(@NotBlank(message = "값이 비어있을 수 없습니다.")
                                String rankType
) {

}
