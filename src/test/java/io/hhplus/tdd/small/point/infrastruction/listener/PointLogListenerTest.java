package io.hhplus.tdd.small.point.infrastruction.listener;

import io.hhplus.tdd.point.domain.event.PointLogEvent;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.PointLog;
import io.hhplus.tdd.point.domain.model.TransactionType;
import io.hhplus.tdd.point.infrastruction.listener.PointLogListener;
import io.hhplus.tdd.point.usecase.port.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class PointLogListenerTest {

    @Mock
    private PointRepository pointRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 이벤트를_받으면_로그를_저장한다() throws Exception {
        //given
        Point point = new Point(1L, 1000L, 1234L);
        TransactionType transactionType = TransactionType.CHARGE;

        PointLogEvent pointLogEvent = new PointLogEvent(this, point, transactionType);
        PointLogListener pointLogListener = new PointLogListener(pointRepository);
        when(pointRepository.writeLog(point, transactionType)).thenReturn(mock(PointLog.class));

        //when
        pointLogListener.handlePointLog(pointLogEvent);

        //then
        verify(pointRepository).writeLog(point, transactionType);
    }
}