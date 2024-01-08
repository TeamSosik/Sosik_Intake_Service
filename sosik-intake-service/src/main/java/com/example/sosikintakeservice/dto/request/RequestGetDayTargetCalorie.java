package com.example.sosikintakeservice.dto.request;

import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
public record RequestGetDayTargetCalorie(Long memberId,
                                         LocalDateTime createdAt) {
}
