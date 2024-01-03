package com.example.sosikintakeservice.dto.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record RequestGetIntake(
    Long memberId,
    LocalDateTime createdAt
){
}
