package io.hhplus.tdd.point.domain.model;


import lombok.Builder;
import lombok.Getter;

@Getter
public class Point {

    private final Long userId;
    private final Long amount;
    private Long updateMillis;

    @Builder
    public Point(Long userId, Long amount, Long updateMillis) {
        this.userId = userId;
        this.amount = amount;
        this.updateMillis = updateMillis;
    }

    public Point charge(long amount) {
        long updatePoint = this.amount + amount;
        return Point.builder()
                .userId(this.userId)
                .amount(updatePoint)
                .build();
    }

    public Point use(long amount) {
        long updatePoint = this.amount - amount;
        return Point.builder()
                .userId(this.getUserId())
                .amount(updatePoint)
                .build();
    }
}
