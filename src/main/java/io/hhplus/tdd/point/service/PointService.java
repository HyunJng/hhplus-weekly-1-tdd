package io.hhplus.tdd.point.service;

import io.hhplus.tdd.common.exception.domain.CommonException;
import io.hhplus.tdd.common.exception.domain.ErrorCode;
import io.hhplus.tdd.common.time.TimeHolder;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
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
    private final TimeHolder timeHolder;

    public UserPoint findUserPoint(Long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> findUserPointLog(Long id) {
        return pointHistoryTable.selectAllByUserId(id).stream()
                .sorted(Comparator.comparingLong(PointHistory::id).reversed())
                .toList();
    }

    public UserPoint charge(Long id, Long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        if (!userPoint.enableChargeAmount(amount)) {
            throw new CommonException(ErrorCode.POLICY_VIOLATION, "포인트 충전 금액");
        }
        UserPoint chargeUserPoint = userPoint.charge(amount, timeHolder);
        userPointTable.insertOrUpdate(chargeUserPoint.id(), chargeUserPoint.point());

        saveLog(chargeUserPoint, TransactionType.CHARGE);
        return chargeUserPoint;
    }

    public UserPoint use(Long id, Long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        if (!userPoint.enableUseAmount(amount)) {
            throw new CommonException(ErrorCode.POLICY_VIOLATION, "포인트 사용 금액");
        }
        UserPoint usedUserPoint = userPoint.use(amount, timeHolder);
        userPointTable.insertOrUpdate(usedUserPoint.id(), usedUserPoint.point());

        saveLog(usedUserPoint, TransactionType.USE);
        return usedUserPoint;
    }

    private void saveLog(UserPoint userPoint, TransactionType type) {
        pointHistoryTable.insert(userPoint.id(), userPoint.point(), type, userPoint.updateMillis());
    }
}
