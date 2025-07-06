package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.infrastruction.PointHistoryTable;
import io.hhplus.tdd.point.infrastruction.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PointServiceTest {

    private PointService pointService;
    private UserPointTable pointTable;
    private PointHistoryTable pointHistoryTables;

    @BeforeEach
    void init() {
        pointTable = new UserPointTable();
        pointHistoryTables = new PointHistoryTable();
        pointService = new PointService(pointTable, pointHistoryTables);

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
}