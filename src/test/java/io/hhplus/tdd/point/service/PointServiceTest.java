package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.infrastruction.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointServiceTest {

    private PointService pointService;
    private UserPointTable pointTable;

    @BeforeEach
    void init() {
        pointTable = new UserPointTable();
        pointService = new PointService(pointTable);

        pointTable.insertOrUpdate(1L, 1000);
        pointTable.insertOrUpdate(2L, 80000);
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

}