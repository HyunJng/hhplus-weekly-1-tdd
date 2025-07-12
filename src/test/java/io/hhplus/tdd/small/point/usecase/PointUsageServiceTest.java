package io.hhplus.tdd.small.point.usecase;

import io.hhplus.tdd.point.domain.event.PointLogEvent;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.policy.UsagePolicy;
import io.hhplus.tdd.point.usecase.PointUsageService;
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

/**
 * 도메인과 서비스가 참조하는 객체들이 의도한대로 협력하고 데이터를 처리하였는지 테스트
 * mock을 이용하여 도메인 테스트와 불필요한 중복을 피하도록 의도
 * */
class PointUsageServiceTest {

    private PointUsageService pointUsageService;

    @Mock
    private PointRepository pointRepository;
    @Mock
    private UsagePolicy usagePolicy;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        pointUsageService = new PointUsageService(pointRepository, usagePolicy, eventPublisher);
    }

    /**
     * repository와 올바른 순서로 협력하는지 확인하고자 작성
     * */
    @Test
    void 사용_요청시_포인트가_차감되고_로그_이벤트를_발행한다() throws Exception {
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
        Point result = pointUsageService.use(1L, usageAmount);

        //then
        assertThat(result).isEqualTo(expectResult);

        ArgumentCaptor<Point> saveCaptor = ArgumentCaptor.forClass(Point.class);
        verify(pointRepository).saveAndUpdate(saveCaptor.capture());
        Point savePoint = saveCaptor.getValue();
        assertThat(savePoint.getUserId()).isEqualTo(1L);
        assertThat(savePoint.getAmount()).isEqualTo(5000L);

        ArgumentCaptor<PointLogEvent> pointLogEventCaptor = ArgumentCaptor.forClass(PointLogEvent.class);
        verify(eventPublisher).publishEvent(pointLogEventCaptor.capture());

    }
}