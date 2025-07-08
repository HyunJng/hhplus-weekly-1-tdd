package io.hhplus.tdd.point.infrastruction.database.entity;

import io.hhplus.tdd.point.domain.model.PointLog;
import io.hhplus.tdd.point.domain.model.TransactionType;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {

    public PointLog to() {
        return PointLog.builder()
                .id(this.id)
                .userId(this.userId)
                .point(this.amount)
                .type(this.type)
                .updateMillis(this.updateMillis)
                .build();
    }
}
