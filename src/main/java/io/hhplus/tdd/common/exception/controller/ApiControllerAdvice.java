package io.hhplus.tdd.common.exception.controller;

import io.hhplus.tdd.common.exception.domain.CommonException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException e) {
        HttpStatus status = e.getErrorCode().getStatus();
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(String.valueOf(status.value()), e.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }
}
