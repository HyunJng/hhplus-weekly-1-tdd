package io.hhplus.tdd.large.point;

import io.hhplus.tdd.point.domain.event.PointLogEvent;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.TransactionType;
import io.hhplus.tdd.point.infrastruction.listener.PointLogListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class PointLogEventIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private PointLogListener pointLogListener;

    @Test
    void 로그_저장_이벤트_리스너가_동작한다() throws Exception {
        //given
        Point point = mock(Point.class);
        TransactionType type = mock(TransactionType.class);

        PointLogEvent pointLogEvent = new PointLogEvent(this, point, type);

        //when
        eventPublisher.publishEvent(pointLogEvent);

        //then
        verify(pointLogListener).handlePointLog(pointLogEvent);
    }
}
