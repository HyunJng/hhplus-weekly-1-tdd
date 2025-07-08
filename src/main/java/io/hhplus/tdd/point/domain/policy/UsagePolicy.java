package io.hhplus.tdd.point.domain.policy;

public interface UsagePolicy {
    void validate(long amount, long currentPoint);
}
