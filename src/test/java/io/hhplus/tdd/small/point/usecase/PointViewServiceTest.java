package io.hhplus.tdd.small.point.usecase;

import io.hhplus.tdd.common.exception.domain.CommonException;
import io.hhplus.tdd.common.exception.domain.ErrorCode;
import io.hhplus.tdd.mock.PointFixture;
import io.hhplus.tdd.mock.PointLogFixture;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.PointLog;
import io.hhplus.tdd.point.domain.model.TransactionType;
import io.hhplus.tdd.point.usecase.PointViewService;
import io.hhplus.tdd.point.usecase.port.PointRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PointViewServiceTest {

    private PointViewService pointViewService;

    @Mock
    private PointRepository pointRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        pointViewService = new PointViewService(pointRepository);
    }

    /**
     * repository와 협력하는지, 반환된 값을 응답하는지 테스트하고자 작성
     * */
    @Test
    void 포인트를_조회할_수_있다() throws Exception {
        //given
        Point logPoint = PointFixture.createDefaultPoint();
        when(pointRepository.findByUserId(1L)).thenReturn(Optional.of(logPoint));

        //when
        Point point = pointViewService.findPoint(logPoint.getUserId());

        //then
        verify(pointRepository).findByUserId(1l);
        assertThat(point.getUserId()).isEqualTo(logPoint.getUserId());
        assertThat(point.getAmount()).isEqualTo(logPoint.getAmount());
    }

    /**
     * 해당 메서드에서 던지는 Exception 이 제대로 반환되었는지 확인하고자 작성
     * */
    @Test
    void 포인트_정보가_존재하지_않는다면_오류를_발생시킨다() throws Exception {
        //given & when & then
        Assertions.assertThatThrownBy(() -> pointViewService.findPoint(1000L))
                .isInstanceOf(CommonException.class)
                .hasMessage(ErrorCode.NOT_FOUND_RESOURCE.getMessage("user"));
    }

    /**
     * repository와 협력하는지, 반환된 값을 적절히 정렬하였는지 확인하고자 작성
     * */
    @Test
    void 포인트_이용_내역을_최신순으로_조회할_수_있다() throws Exception {
        //given
        PointLog pointLog1 = PointLogFixture.createPointLog(10_000, TransactionType.CHARGE);
        PointLog pointLog2 = PointLogFixture.createPointLog(5000, TransactionType.USE);
        PointLog pointLog3 = PointLogFixture.createPointLog(1000, TransactionType.CHARGE);

        when(pointRepository.findPointLogsByUserId(1L))
                .thenReturn(List.of(pointLog1, pointLog2, pointLog3));

        //when
        List<PointLog> pointLogs = pointViewService.findPointLogDesc(1L);

        //then
        assertThat(pointLogs.size()).isEqualTo(3);

        assertThat(pointLogs.get(0).getUserId()).isEqualTo(pointLog3.getUserId());
        assertThat(pointLogs.get(0).getPoint()).isEqualTo(pointLog3.getPoint());
        assertThat(pointLogs.get(0).getType()).isEqualTo(pointLog3.getType());

        assertThat(pointLogs.get(1).getUserId()).isEqualTo(pointLog2.getUserId());
        assertThat(pointLogs.get(1).getPoint()).isEqualTo(pointLog2.getPoint());
        assertThat(pointLogs.get(1).getType()).isEqualTo(pointLog2.getType());

        assertThat(pointLogs.get(2).getUserId()).isEqualTo(pointLog1.getUserId());
        assertThat(pointLogs.get(2).getPoint()).isEqualTo(pointLog1.getPoint());
        assertThat(pointLogs.get(2).getType()).isEqualTo(pointLog1.getType());
    }

}