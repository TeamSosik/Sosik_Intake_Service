package com.example.sosikintakeservice.controller;

import com.example.sosikintakeservice.exception.ApplicationException;
import com.example.sosikintakeservice.exception.ErrorResult;
import com.example.sosikintakeservice.exception.FieldErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class IntakeControllerAdvice {

    // 파라미터 검증 예외
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<FieldErrorResult> MethodArgumentNotValidEx(MethodArgumentNotValidException e) {

        FieldErrorResult body = FieldErrorResult.builder()
                .field(e.getFieldError().getField())
                .code(e.getStatusCode())
                .message(e.getFieldError().getDefaultMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(value = {ApplicationException.class})
    public ResponseEntity<ErrorResult> ApplicationEx(ApplicationException e) {

        ErrorResult body = ErrorResult.builder()
                .code(e.getErrorCode().getStatus())
                .message(e.getErrorCode().getMessage())
                .build();

        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(body);

    }






}
