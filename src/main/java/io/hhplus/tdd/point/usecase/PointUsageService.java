package io.hhplus.tdd.point.usecase;

import io.hhplus.tdd.common.exception.domain.CommonException;
import io.hhplus.tdd.common.exception.domain.ErrorCode;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.TransactionType;
import io.hhplus.tdd.point.domain.policy.UsagePolicy;
import io.hhplus.tdd.point.usecase.port.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Service
public class PointUsageService {

    private final PointRepository pointRepository;
    private final UsagePolicy usagePolicy;

    Lock lock = new ReentrantLock();

    public Point use(Long id, Long amount) {
        lock.lock();
        try {
            Point point = pointRepository.findByUserId(id)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE, "user"));
            Point usedPoint = point.use(amount, usagePolicy);
            Point result = pointRepository.saveAndUpdate(usedPoint);

            pointRepository.writeLog(result, TransactionType.USE);
            return result;
        } finally {
            lock.unlock();
        }
    }

}
