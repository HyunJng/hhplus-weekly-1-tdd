package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.infrastruction.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointService {

    private final UserPointTable userPointTable;

    public UserPoint findUserPoint(Long id) {
        return userPointTable.selectById(id);
    }
}
