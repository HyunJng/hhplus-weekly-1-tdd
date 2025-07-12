package io.hhplus.tdd.mock;

import io.hhplus.tdd.point.domain.model.Point;

public class PointFixture {

    public static Point createDefaultPoint() {
        return Point.builder()
                .userId(1L)
                .amount(1000L)
                .updateMillis(System.currentTimeMillis())
                .build();

    }

    public static Point createPoint(long amount) {
        return Point.builder()
                .userId(1L)
                .amount(amount)
                .updateMillis(System.currentTimeMillis())
                .build();

    }
}
