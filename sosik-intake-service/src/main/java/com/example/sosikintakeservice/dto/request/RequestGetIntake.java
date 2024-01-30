package com.example.sosikintakeservice.dto.request;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record RequestGetIntake(Long memberId,
                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                               LocalDate createdAt
){

}
