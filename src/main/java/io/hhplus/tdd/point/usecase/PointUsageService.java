package io.hhplus.tdd.point.usecase;

import io.hhplus.tdd.common.exception.domain.CommonException;
import io.hhplus.tdd.common.exception.domain.ErrorCode;
import io.hhplus.tdd.point.domain.event.PointLogEvent;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.TransactionType;
import io.hhplus.tdd.point.domain.policy.UsagePolicy;
import io.hhplus.tdd.point.usecase.port.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointUsageService {

    private final PointRepository pointRepository;
    private final UsagePolicy usagePolicy;
    private final ApplicationEventPublisher eventPublisher;

    public Point use(Long id, Long amount) {
        Point point = pointRepository.findByUserId(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE, "user"));
        Point usedPoint = point.use(amount, usagePolicy);
        Point result = pointRepository.saveAndUpdate(usedPoint);

        PointLogEvent pointLogEvent = new PointLogEvent(this, result, TransactionType.CHARGE);
        eventPublisher.publishEvent(pointLogEvent);
        return result;
    }

}
