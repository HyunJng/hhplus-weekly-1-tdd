package io.hhplus.tdd.common.exception.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND_RESOURCE(HttpStatus.BAD_REQUEST, "존재하지 않은 %1"),
    POLICY_VIOLATION(HttpStatus.BAD_REQUEST, "정책 위반(%1)"),
    SHORTAGE_RESOURCE(HttpStatus.BAD_REQUEST, "%1 부족");

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
