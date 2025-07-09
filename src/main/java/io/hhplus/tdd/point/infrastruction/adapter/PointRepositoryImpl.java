package io.hhplus.tdd.point.infrastruction.adapter;

import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.PointLog;
import io.hhplus.tdd.point.domain.model.TransactionType;
import io.hhplus.tdd.point.infrastruction.database.PointHistoryTable;
import io.hhplus.tdd.point.infrastruction.database.UserPointTable;
import io.hhplus.tdd.point.infrastruction.database.entity.PointHistory;
import io.hhplus.tdd.point.service.port.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PointRepositoryImpl implements PointRepository {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    @Override
    public Optional<Point> findByUserId(Long userId) {
        return Optional.ofNullable(userPointTable.selectById(userId).to());
    }

    @Override
    public List<PointLog> findPointLogsByUserId(Long userId) {
        return pointHistoryTable.selectAllByUserId(userId).stream()
                .map(PointHistory::to)
                .toList();
    }

    @Override
    public Point saveAndUpdate(Point point) {
        return userPointTable.insertOrUpdate(point.getUserId(), point.getAmount()).to();
    }

    @Override
    public PointLog writeLog(Point point, TransactionType type) {
        return pointHistoryTable.insert(point.getUserId(), point.getAmount(), type, System.currentTimeMillis()).to();
    }
}
