package io.hhplus.tdd.point.domain.policy;

import io.hhplus.tdd.common.exception.domain.CommonException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultChargingPolicyTest {

    @ParameterizedTest(name = "포인트 {0}을 충전할 수 있다")
    @ValueSource(longs = {1000L, 5000L, 10_000L})
    void 회_당_1000_이상_10000_이하의_포인트를_충전할_수_있다(long chargeAmount) throws Exception {
        //given
        ChargingPolicy chargingPolicy = new DefaultChargingPolicy();

        //when & then
        assertThatCode(
                () -> chargingPolicy.validate(chargeAmount, 0)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "{0}원은 1회 충전 금액 정책에 어긋나 오류를 발생시킨다")
    @ValueSource(longs = {-1000L, 0L, 999L, 10_001L})
    void 회_당_1000_미만_10000_이상의_포인트를_충전할_수_없다(long chargeAmount) throws Exception {
        //given
        ChargingPolicy chargingPolicy = new DefaultChargingPolicy();

        //when & then
        assertThatThrownBy(() -> chargingPolicy.validate(chargeAmount, 0))
                .isInstanceOf(CommonException.class);
    }

    @Test
    void 최대_잔고_10000_까지_충전할_수_있다() throws Exception {
        // given
        ChargingPolicy chargingPolicy = new DefaultChargingPolicy();
        long currentPoint = 90_000L;
        long chargeAmount = 10_000L;

        // when & then
        assertThatCode(() -> chargingPolicy.validate(chargeAmount, currentPoint))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "{0}원은 최대 잔고 정책에 어긋나 오류를 발생시킨다")
    @ValueSource(longs = {5001L, 10_000L})
    void 최대_잔고_10000을_초과한_충전은_할_수_없다(long chargeAmount) throws Exception {
        //given
        ChargingPolicy chargingPolicy = new DefaultChargingPolicy();
        long currentPoint = 95_000L;

        //when & then
        assertThatThrownBy(() -> chargingPolicy.validate(chargeAmount, currentPoint))
                .isInstanceOf(CommonException.class);
    }

}