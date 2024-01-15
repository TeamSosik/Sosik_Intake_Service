package com.example.sosikintakeservice.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
public record RequestTargetCalorie( @NotNull
                                    Integer dayTargetKcal

) {
}
