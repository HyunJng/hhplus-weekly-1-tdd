package io.hhplus.tdd.point.domain.policy;

import io.hhplus.tdd.common.exception.domain.CommonException;
import io.hhplus.tdd.common.exception.domain.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class DefaultChargingPolicy implements ChargingPolicy {

    private final static Long CHARGE_MIN = 1000L;
    private final static Long CHARGE_MAX = 10_000L;
    private final static Long POINT_BALANCE_MAX = 100_000L;

    public void validate(long amount, long currentPoint) {
        if (amount < CHARGE_MIN) throw new CommonException(ErrorCode.POLICY_VIOLATION, "1회 충전 가능 금액 미만");
        if (amount > CHARGE_MAX) throw new CommonException(ErrorCode.POLICY_VIOLATION, "1회 충전 가능 금액 초과");
        if (currentPoint + amount > POINT_BALANCE_MAX) throw new CommonException(ErrorCode.POLICY_VIOLATION, "포인트 가능 잔액 초과");
    }
}