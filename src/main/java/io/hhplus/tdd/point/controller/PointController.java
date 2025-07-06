package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;

    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        return pointService.findUserPoint(id);
    }

    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        return pointService.findUserPointLog(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return new UserPoint(0, 0, 0);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return new UserPoint(0, 0, 0);
    }
}
