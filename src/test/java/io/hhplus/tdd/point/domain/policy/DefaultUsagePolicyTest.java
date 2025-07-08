package io.hhplus.tdd.point.domain.policy;

import io.hhplus.tdd.common.exception.domain.CommonException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultUsagePolicyTest {

    @ParameterizedTest(name = "포인트 {0}을 사용할 수 있다")
    @ValueSource(longs = {1000L, 50_000L, 100_000})
    void 회_당_1000_이상의_포인트를_사용할_수_있다(long usageAmount) throws Exception {
        //given
        UsagePolicy usagePolicy = new DefaultUsagePolicy();

        //when & then
        assertThatCode(
                () -> usagePolicy.validate(usageAmount, 100_000)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "{0}원은 1회 사용 금액 정책에 어긋나 오류를 발생시킨다")
    @ValueSource(longs = {-1000L, 0L, 900L, 999L})
    void 회_당_1000_미만의_포인트를_사용_시도하면_오류를_발생시킨다(long usageAmount) throws Exception {
        //given
        UsagePolicy usagePolicy = new DefaultUsagePolicy();

        //when & then
        assertThatThrownBy(() -> usagePolicy.validate(usageAmount, 100_000))
                .isInstanceOf(CommonException.class);
    }

    @ParameterizedTest(name = "잔고가 {0}이상이면 포인트를 사용할 수 있다")
    @ValueSource(longs = {10000L, 50_000L, 100_000})
    void 잔고가_10000이상이면_포인트를_사용할_수_있다(long currentPoint) throws Exception {
        //given
        UsagePolicy usagePolicy = new DefaultUsagePolicy();

        //when & then
        assertThatCode(
                () -> usagePolicy.validate(1000, currentPoint)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "잔고가 {0}이하이면 최소 잔액 정책에 어긋나 오류를 발생시킨다")
    @ValueSource(longs = {0L, 900L, 9999L})
    void 잔고가_10000_이하이면_포인트를_사용할_수_없다(long currentPoint) throws Exception {
        //given
        UsagePolicy usagePolicy = new DefaultUsagePolicy();

        //when & then
        assertThatThrownBy(() -> usagePolicy.validate(1000, currentPoint))
                .isInstanceOf(CommonException.class);
    }

    @Test
    void 잔고를_초과한_금액은_사용할_수_없다() throws Exception {
        //given
        UsagePolicy usagePolicy = new DefaultUsagePolicy();
        long currentPoint = 1000L;
        long usageAmount = 10_000L;

        //when & then
        assertThatThrownBy(() -> usagePolicy.validate(usageAmount, currentPoint))
                .isInstanceOf(CommonException.class);
    }
}