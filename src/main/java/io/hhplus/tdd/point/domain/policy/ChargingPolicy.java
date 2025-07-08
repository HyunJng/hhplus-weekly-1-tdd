package io.hhplus.tdd.point.domain.policy;

public interface ChargingPolicy {
    void validate(long amount, long currentPoint);
}