package com.example.sosikintakeservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TARGETCALORIE_NOT_FOUND(HttpStatus.NOT_FOUND,"일일목표칼로리를 찾지 못했습니다"),
    DTO_EMPTY_COLUMN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"객체에 null값이 있습니다"),
    EXISTENCE_TARGETCALORIE_ERROR(HttpStatus.CONFLICT,"이미 일일목표칼로리를 기입했습니다"),
    INTAKE_NOT_FOUND(HttpStatus.NOT_FOUND,"음식을 찾지 못했습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"내부 서버의 오류입니다.");

    private HttpStatus status;
    private String message;
}
