package io.hhplus.tdd.point.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Point의 역할을 테스트
 * 부여된 역할에 따라 상태 변경을 해내는지 확인하는 목적.
 * */
class PointTest {

    /**
     * 충전 시 기존 포인트의에서 충전한 금액이 더해진 포인트 객체가 반환되는지 확인하고자 작성
     * */
    @ParameterizedTest(name = "포인트 {0}을 충전하면 {0}만큼 증가한 포인트를 반환한다")
    @ValueSource(longs = {1000L, 5000L, 10_000L})
    void 포인트를_충전하면_금액이_증가한_포인트를_반환한다(long chargeAmount) throws Exception {
        //given
        Point currentPoint = new Point(1L, 0L, 1234L);

        //when
        Point chargePoint = currentPoint.charge(chargeAmount);

        //then
        assertThat(chargePoint.getUserId()).isEqualTo(1L);
        assertThat(chargePoint.getAmount()).isEqualTo(chargeAmount);
    }

    /**
     * 사용 시 기존 포인트의에서 사용한 금액이 차감된 포인트 객체가 반환되는지 확인하고자 작성
     * */
    @ParameterizedTest(name = "포인트 {0}을 사용하면 {0}만큼 차감한 포인트를 반환한다")
    @ValueSource(longs = {1000L, 10_000L, 100_000L})
    void 포인트를_사용하면_금액이_차감된_포인트를_반환한다(long usageAmount) throws Exception {
        //given
        Point currentPoint = new Point(1L, 100_000L, 1234L);

        //when
        Point usagePoint = currentPoint.use(usageAmount);

        //then
        assertThat(usagePoint.getUserId()).isEqualTo(1L);
        assertThat(usagePoint.getAmount()).isEqualTo(100_000L - usageAmount);
    }
}