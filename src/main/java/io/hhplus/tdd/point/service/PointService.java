package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.infrastruction.PointHistoryTable;
import io.hhplus.tdd.point.infrastruction.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint findUserPoint(Long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> findUserPointLog(Long id) {
        return pointHistoryTable.selectAllByUserId(id).stream()
                .sorted(Comparator.comparingLong(PointHistory::id).reversed())
                .toList();
    }
}
