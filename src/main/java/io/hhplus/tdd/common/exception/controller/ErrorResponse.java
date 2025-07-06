package io.hhplus.tdd.common.exception.controller;

public record ErrorResponse(
        String code,
        String message
) {
}
