package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.common.time.TimeHolder;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    private final static Long CHARGE_MIN = 1000L;
    private final static Long CHARGE_MAX = 10000L;
    private final static Long USE_MIN = 1000L;
    private final static Long BALANCE_MIN_FOR_USE = 10000L;
    private final static Long POINT_BALANCE_MAX = 100000L;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public boolean enableChargeAmount(long amount) {
        boolean result = true;
        if (amount < CHARGE_MIN || CHARGE_MAX < amount) {
            result = false;
        } else if(this.point + amount > POINT_BALANCE_MAX){
            result = false;
        }

        return result;
    }

    public UserPoint charge(long amount, TimeHolder timeHolder) {
        long updatePoint = this.point + amount;
        return new UserPoint(this.id, updatePoint, timeHolder.currentTimeMillis());
    }

    public boolean enableUseAmount(Long amount) {
        boolean result = true;
        if (this.point < BALANCE_MIN_FOR_USE) {
            result = false;
        } else if (amount < USE_MIN) {
            result = false;
        } else if (this.point - amount < 0) {
            result = false;
        }
        return result;
    }

    public UserPoint use(long amount, TimeHolder timeHolder) {
        long updatePoint = this.point - amount;
        return new UserPoint(this.id, updatePoint, timeHolder.currentTimeMillis());
    }
}
