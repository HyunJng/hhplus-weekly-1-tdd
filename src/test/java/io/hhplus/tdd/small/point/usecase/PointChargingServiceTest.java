package io.hhplus.tdd.small.point.usecase;

import io.hhplus.tdd.point.domain.event.PointLogEvent;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.policy.ChargingPolicy;
import io.hhplus.tdd.point.usecase.PointChargingService;
import io.hhplus.tdd.point.usecase.port.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PointChargingServiceTest {

    private PointChargingService pointChargingService;

    @Mock
    private PointRepository pointRepository;
    @Mock
    private ChargingPolicy chargingPolicy;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        pointChargingService = new PointChargingService(pointRepository, chargingPolicy, eventPublisher);
    }

    /**
     * repository와 올바른 순서로 협력하는지 확인하고자 작성
     * */
    @Test
    void 충전_요청시_포인트가_저장되고_로그_이벤트를_발행한다() throws Exception {
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
        Point result = pointChargingService.charge(1L, chargeAmount);

        //then
        assertThat(result).isEqualTo(expectResult);

        ArgumentCaptor<Point> saveCaptor = ArgumentCaptor.forClass(Point.class);
        verify(pointRepository).saveAndUpdate(saveCaptor.capture());
        Point savePoint = saveCaptor.getValue();
        assertThat(savePoint.getUserId()).isEqualTo(1L);
        assertThat(savePoint.getAmount()).isEqualTo(15000L);

        ArgumentCaptor<PointLogEvent> pointLogEventCaptor = ArgumentCaptor.forClass(PointLogEvent.class);
        verify(eventPublisher).publishEvent(pointLogEventCaptor.capture());
    }

}