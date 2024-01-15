package com.example.sosikintakeservice.dto.request;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record RequestGetIntake(
    Long memberId,
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime createdAt
){
}
