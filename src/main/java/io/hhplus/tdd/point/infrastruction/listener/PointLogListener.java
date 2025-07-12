package io.hhplus.tdd.point.infrastruction.listener;

import io.hhplus.tdd.point.domain.event.PointLogEvent;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.TransactionType;
import io.hhplus.tdd.point.usecase.port.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointLogListener {

    private final PointRepository pointRepository;

    @Async
    @EventListener
    public void handlePointLog(PointLogEvent pointLogEvent) {
        Point point = pointLogEvent.getPoint();
        TransactionType transactionType = pointLogEvent.getTransactionType();

        pointRepository.writeLog(point, transactionType);
    }
}
