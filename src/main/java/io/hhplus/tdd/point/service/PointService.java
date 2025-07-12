package io.hhplus.tdd.point.service;

import io.hhplus.tdd.common.exception.domain.CommonException;
import io.hhplus.tdd.common.exception.domain.ErrorCode;
import io.hhplus.tdd.point.domain.policy.ChargingPolicy;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.PointLog;
import io.hhplus.tdd.point.domain.model.TransactionType;
import io.hhplus.tdd.point.domain.policy.UsagePolicy;
import io.hhplus.tdd.point.service.port.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Service
public class PointService {

    private final PointRepository pointRepository;
    private final ChargingPolicy chargingPolicy;
    private final UsagePolicy usagePolicy;

    Lock lock = new ReentrantLock();

    public Point findPoint(Long id) {
        return pointRepository.findByUserId(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE, "user"));
    }

    public List<PointLog> findPointLogDesc(Long id) {
        return pointRepository.findPointLogsByUserId(id).stream()
                .sorted(Comparator.comparingLong(PointLog::getId).reversed())
                .toList();
    }

    public Point charge(Long id, Long amount) {
        lock.lock();
        try {
            Point point = pointRepository.findByUserId(id)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE, "user"));

            Point chargePoint = point.charge(amount, chargingPolicy);
            Point result = pointRepository.saveAndUpdate(chargePoint);

            pointRepository.writeLog(result, TransactionType.CHARGE);
            return result;
        } finally {
            lock.unlock();
        }
    }

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
