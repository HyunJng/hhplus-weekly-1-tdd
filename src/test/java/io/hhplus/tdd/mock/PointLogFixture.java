package io.hhplus.tdd.mock;

import io.hhplus.tdd.point.domain.model.PointLog;
import io.hhplus.tdd.point.domain.model.TransactionType;

import java.util.concurrent.atomic.AtomicLong;

public class PointLogFixture {

    private static final AtomicLong atomicLong = new AtomicLong();

    public static PointLog createPointLog(long point, TransactionType type) {
        return PointLog.builder()
                .id(atomicLong.incrementAndGet())
                .userId(1L)
                .point(point)
                .type(type)
                .updateMillis(System.currentTimeMillis())
                .build();
    }
}
