package io.hhplus.tdd.small.point.service;

import io.hhplus.tdd.common.exception.domain.CommonException;
import io.hhplus.tdd.common.exception.domain.ErrorCode;
import io.hhplus.tdd.mock.PointFixture;
import io.hhplus.tdd.mock.PointLogFixture;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.PointLog;
import io.hhplus.tdd.point.domain.model.TransactionType;
import io.hhplus.tdd.point.domain.policy.ChargingPolicy;
import io.hhplus.tdd.point.domain.policy.UsagePolicy;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.service.port.PointRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * 도메인과 서비스가 참조하는 객체들이 의도한대로 협력하고 데이터를 처리하였는지 테스트
 * mock을 이용하여 도메인 테스트와 불필요한 중복을 피하도록 의도
 * */
class PointServiceTest {

    private PointService pointService;

    @Mock
    private PointRepository pointRepository;
    @Mock
    private ChargingPolicy chargingPolicy;
    @Mock
    private UsagePolicy usagePolicy;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        pointService = new PointService(pointRepository, chargingPolicy, usagePolicy);
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
        Point point = pointService.findPoint(logPoint.getUserId());

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
        Assertions.assertThatThrownBy(() -> pointService.findPoint(1000L))
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
        List<PointLog> pointLogs = pointService.findPointLogDesc(1L);

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

    /**
     * repository와 올바른 순서로 협력하는지 확인하고자 작성
     * */
    @Test
    void 충전_요청시_포인트가_저장되고_이력이_기록된다() throws Exception {
        //given
        long currentAmount = 10000L;
        long chargeAmount = 5000L;

        Point currentPoint = mock(Point.class);
        Point chargePoint = new Point(1L, 15000L, 1234L);
        Point expectResult = new Point(1L, 15000L, 1234L);

        when(pointRepository.findByUserId(1L)).thenReturn(Optional.of(currentPoint));
        when(currentPoint.getAmount()).thenReturn(currentAmount);
        when(currentPoint.charge(chargeAmount, chargingPolicy)).thenReturn(chargePoint);
        when(pointRepository.saveAndUpdate(chargePoint)).thenReturn(expectResult);
        //when
        Point result = pointService.charge(1L, chargeAmount);

        //then
        assertThat(result).isEqualTo(expectResult);

        ArgumentCaptor<Point> saveCaptor = ArgumentCaptor.forClass(Point.class);
        verify(pointRepository).saveAndUpdate(saveCaptor.capture());
        Point savePoint = saveCaptor.getValue();
        assertThat(savePoint.getUserId()).isEqualTo(1L);
        assertThat(savePoint.getAmount()).isEqualTo(15000L);

        ArgumentCaptor<Point> logCaptor = ArgumentCaptor.forClass(Point.class);
        ArgumentCaptor<TransactionType> typeCaptor = ArgumentCaptor.forClass(TransactionType.class);
        verify(pointRepository).writeLog(logCaptor.capture(), typeCaptor.capture());
        Point logPoint = logCaptor.getValue();
        assertThat(logPoint).isEqualTo(expectResult);
        assertThat(typeCaptor.getValue()).isEqualTo(TransactionType.CHARGE);
    }

    /**
     * repository와 올바른 순서로 협력하는지 확인하고자 작성
     * */
    @Test
    void 사용_요청시_포인트가_차감되고_이력이_기록된다() throws Exception {
        //given
        long currentAmount = 10000L;
        long usageAmount = 5000L;

        Point currentPoint = mock(Point.class);
        Point usagePoint = new Point(1L, 5000L, 1234L);
        Point expectResult = new Point(1L, 5000L, 1234L);

        when(pointRepository.findByUserId(1L)).thenReturn(Optional.of(currentPoint));
        when(currentPoint.getAmount()).thenReturn(currentAmount);
        when(currentPoint.use(usageAmount, usagePolicy)).thenReturn(usagePoint);
        when(pointRepository.saveAndUpdate(usagePoint)).thenReturn(expectResult);

        //when
        Point result = pointService.use(1L, usageAmount);

        //then
        assertThat(result).isEqualTo(expectResult);

        ArgumentCaptor<Point> saveCaptor = ArgumentCaptor.forClass(Point.class);
        verify(pointRepository).saveAndUpdate(saveCaptor.capture());
        Point savePoint = saveCaptor.getValue();
        assertThat(savePoint.getUserId()).isEqualTo(1L);
        assertThat(savePoint.getAmount()).isEqualTo(5000L);

        ArgumentCaptor<Point> logCaptor = ArgumentCaptor.forClass(Point.class);
        ArgumentCaptor<TransactionType> typeCaptor = ArgumentCaptor.forClass(TransactionType.class);
        verify(pointRepository).writeLog(logCaptor.capture(), typeCaptor.capture());
        Point logPoint = logCaptor.getValue();
        assertThat(logPoint).isEqualTo(expectResult);
        assertThat(typeCaptor.getValue()).isEqualTo(TransactionType.USE);
    }
}