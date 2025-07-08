package io.hhplus.tdd.point.domain.policy;

import io.hhplus.tdd.common.exception.domain.CommonException;
import io.hhplus.tdd.common.exception.domain.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class DefaultUsagePolicy implements UsagePolicy{

    private final static Long USE_MIN = 1000L;
    private final static Long BALANCE_MIN_FOR_USE = 10_000L;

    @Override
    public void validate(long amount, long currentPoint) {
        if (amount < USE_MIN) throw new CommonException(ErrorCode.POLICY_VIOLATION, "최소 사용 금액 미만");
        if (currentPoint < BALANCE_MIN_FOR_USE) throw new CommonException(ErrorCode.POLICY_VIOLATION, "최소 잔액 미만");
        if (currentPoint - amount < 0) throw new CommonException(ErrorCode.SHORTAGE_RESOURCE, "잔액");
    }
}
