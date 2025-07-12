package io.hhplus.tdd.point.usecase;

import io.hhplus.tdd.common.exception.domain.CommonException;
import io.hhplus.tdd.common.exception.domain.ErrorCode;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.PointLog;
import io.hhplus.tdd.point.usecase.port.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PointViewService {

    private final PointRepository pointRepository;

    public Point findPoint(Long id) {
        return pointRepository.findByUserId(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_RESOURCE, "user"));
    }

    public List<PointLog> findPointLogDesc(Long id) {
        return pointRepository.findPointLogsByUserId(id).stream()
                .sorted(Comparator.comparingLong(PointLog::getId).reversed())
                .toList();
    }

}
