package com.example.sosikintakeservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateTargetCalorie(Long Id,
                                  @NotNull
                                  Integer dayTargetKcal
                                ) {

}
