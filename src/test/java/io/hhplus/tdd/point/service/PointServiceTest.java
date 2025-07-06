package io.hhplus.tdd.point.service;

import io.hhplus.tdd.common.exception.domain.CommonException;
import io.hhplus.tdd.common.exception.domain.ErrorCode;
import io.hhplus.tdd.common.time.TimeHolder;
import io.hhplus.tdd.mock.TestTimeHolder;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infrastruction.PointHistoryTable;
import io.hhplus.tdd.point.infrastruction.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointServiceTest {

    private PointService pointService;
    private UserPointTable pointTable;
    private PointHistoryTable pointHistoryTables;
    private TimeHolder timeHolder;

    @BeforeEach
    void init() {
        pointTable = new UserPointTable();
        pointHistoryTables = new PointHistoryTable();
        timeHolder = new TestTimeHolder(1234L);
        pointService = new PointService(pointTable, pointHistoryTables, timeHolder);

        pointTable.insertOrUpdate(1L, 1000);
        pointTable.insertOrUpdate(2L, 80000);

        pointHistoryTables.insert(1L, 1000, TransactionType.CHARGE, 1234567891012L);
        pointHistoryTables.insert(2L, 100000, TransactionType.CHARGE, 1334587891012L);
        pointHistoryTables.insert(2L, 30000, TransactionType.USE, 1434587891012L);
        pointHistoryTables.insert(2L, 10000, TransactionType.CHARGE, 1534587891012L);
    }

    @Test
    void 유저의_포인트를_조회할_수_있다() throws Exception {
        //given & when
        UserPoint userPoint = pointService.findUserPoint(1L);

        //then
        assertThat(userPoint.id()).isEqualTo(1L);
        assertThat(userPoint.point()).isEqualTo(1000);
    }

    @Test
    void 존재하지_않는_유저는_포인트_0_을_반환한다() throws Exception {
        //given & when
        UserPoint userPoint = pointService.findUserPoint(1000L);

        //then
        assertThat(userPoint.id()).isEqualTo(1000L);
        assertThat(userPoint.point()).isEqualTo(0);
    }

    @Test
    void 유저의_포인트_이용_내역을_조회할_수_있다() throws Exception {
        //given & when
        List<PointHistory> userPointLog = pointService.findUserPointLog(2L);

        //then
        assertThat(userPointLog.size()).isEqualTo(3);
        Assertions.assertAll(
                () -> assertThat(userPointLog.get(0).userId()).isEqualTo(2L),
                () -> assertThat(userPointLog.get(0).amount()).isEqualTo(10000),
                () -> assertThat(userPointLog.get(0).type()).isEqualTo(TransactionType.CHARGE)
        );
        Assertions.assertAll(
                () ->assertThat(userPointLog.get(1).userId()).isEqualTo(2L),
                () -> assertThat(userPointLog.get(1).amount()).isEqualTo(30000),
                () -> assertThat(userPointLog.get(1).type()).isEqualTo(TransactionType.USE)
        );
        Assertions.assertAll(
                () ->assertThat(userPointLog.get(2).userId()).isEqualTo(2L),
                () -> assertThat(userPointLog.get(2).amount()).isEqualTo(100000),
                () -> assertThat(userPointLog.get(2).type()).isEqualTo(TransactionType.CHARGE)
        );
    }

    @Test
    void 존재하지_않는_유저의_포인트_이용_내역을_조회_요청_시_빈_리스트를_반환한다() throws Exception {
        //given & when
        List<PointHistory> userPointLog = pointService.findUserPointLog(1000L);

        //then
        assertThat(userPointLog.size()).isEqualTo(0);
    }

    @ParameterizedTest(name = "{0}원을 충전하면 금액이 {0}원 증가한다")
    @ValueSource(longs = {1000L, 5000L, 10000L})
    void 유저는_1회에_1000원_이상_10000원_이하의_금액을_충전할_수_있다(long amount) throws Exception {
        //given & when
        UserPoint chargeUserPoint = pointService.charge(1L, amount);

        //then
        assertThat(chargeUserPoint.id()).isEqualTo(1L);
        assertThat(chargeUserPoint.point()).isEqualTo(1000 + amount);
    }


    @ParameterizedTest(name = "{0}원은 충전정책에 어긋나 오류를 반환받는다")
    @ValueSource(longs = {-1000L, 0L, 999L, 10001L})
    void 유저가_1000원_미만_10000원_이상의_금액을_충전_요청하면_오류를_반환한다(long amount) throws Exception {
        //given & when & then
        assertThatThrownBy(() -> pointService.charge(1L, amount))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorCode.POLICY_VIOLATION.getMessage("포인트 충전 금액"));
    }

    @Test
    void 유저는_최대_잔고_100000원까지_충전할_수_있다() throws Exception {
        //given & when
        UserPoint chargeUserPoint1 = pointService.charge(2L, 10000L);
        UserPoint chargeUserPoint2 = pointService.charge(2L, 10000L);

        //then
        assertThat(chargeUserPoint2.id()).isEqualTo(2L);
        assertThat(chargeUserPoint2.point()).isEqualTo(100000L);
    }

    @Test
    void 최대_잔고인_100000원을_초과한_충전_요청이면_오류를_발생시킨다() throws Exception {
        //given
        UserPoint chargeUserPoint1 = pointService.charge(2L, 10000L);
        UserPoint chargeUserPoint2 = pointService.charge(2L, 10000L);

        //when & then
        assertThatThrownBy(() -> pointService.charge(2L, 1000L))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorCode.POLICY_VIOLATION.getMessage("포인트 충전 금액"));
    }

    @Test
    void 유저가_충전을_하면_충전_내역을_저장한다() throws Exception {
        //given & when
        pointService.charge(1L, 1000L);

        //then
        List<PointHistory> pointHistories = pointService.findUserPointLog(1L);
        assertThat(pointHistories.get(0).userId()).isEqualTo(1L);
        assertThat(pointHistories.get(0).amount()).isEqualTo(2000L);
        assertThat(pointHistories.get(0).type()).isEqualTo(TransactionType.CHARGE);
        assertThat(pointHistories.get(0).updateMillis()).isEqualTo(1234L);
    }

}