package io.hhplus.tdd.common.exception.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    POLICY_VIOLATION(HttpStatus.BAD_REQUEST, "%1 정책 위반");

    @Getter
    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage(String... args) {
        String resultMessage = message;
        if (args == null || args.length == 0 || !message.contains("%")) {
            return resultMessage;
        }
        for (int i = 0; i < args.length; i++) {
            resultMessage = resultMessage.replaceAll("%" + (i + 1), args[i]);
        }
        return resultMessage;
    }
}
