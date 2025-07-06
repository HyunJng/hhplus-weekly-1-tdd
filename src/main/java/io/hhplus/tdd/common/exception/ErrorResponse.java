package io.hhplus.tdd.common.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
