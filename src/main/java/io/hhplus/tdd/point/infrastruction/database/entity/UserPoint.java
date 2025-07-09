package io.hhplus.tdd.point.infrastruction.database.entity;

import io.hhplus.tdd.point.domain.model.Point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public Point to() {
        return Point.builder()
                .userId(this.id)
                .amount(this.point)
                .updateMillis(this.updateMillis)
                .build();
    }
}
