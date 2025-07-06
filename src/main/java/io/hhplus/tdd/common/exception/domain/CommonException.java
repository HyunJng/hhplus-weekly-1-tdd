package io.hhplus.tdd.common.exception.domain;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException{

    private final ErrorCode errorCode;

    public CommonException(ErrorCode errorCode, String... args) {
        super(errorCode.getMessage(args));
        this.errorCode = errorCode;
    }
}
