package io.hhplus.tdd.point.usecase.port;

import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.PointLog;
import io.hhplus.tdd.point.domain.model.TransactionType;

import java.util.List;
import java.util.Optional;

public interface PointRepository {

    Optional<Point> findByUserId(Long id);

    List<PointLog> findPointLogsByUserId(Long id);

    Point saveAndUpdate(Point point);

    PointLog writeLog(Point point, TransactionType type);
}
