package io.hhplus.tdd.point.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PointLog {
    private final Long id;
    private final Long userId;
    private final Long point;
    private final TransactionType type;
    private Long updateMillis;

    @Builder
    public PointLog(Long id, Long userId, Long point, TransactionType type, Long updateMillis) {
        this.id = id;
        this.userId = userId;
        this.point = point;
        this.type = type;
        this.updateMillis = updateMillis;
    }
}
