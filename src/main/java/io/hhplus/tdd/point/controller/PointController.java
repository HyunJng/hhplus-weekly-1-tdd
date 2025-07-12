package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.controller.dto.PointDto;
import io.hhplus.tdd.point.controller.dto.PointLogDto;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.usecase.PointChargingService;
import io.hhplus.tdd.point.usecase.PointUsageService;
import io.hhplus.tdd.point.usecase.PointViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/point")
public class PointController {

    private final PointChargingService pointChargingService;
    private final PointUsageService pointUsageService;
    private final PointViewService pointViewService;

    @GetMapping("{id}")
    public ResponseEntity<PointDto.Response> point(
            @PathVariable long id
    ) {
        Point point = pointViewService.findPoint(id);
        return ResponseEntity.ok(PointDto.Response.from(point));
    }

    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointLogDto.Response>> history(
            @PathVariable long id
    ) {
        List<PointLogDto.Response> pointLogs = pointViewService.findPointLogDesc(id).stream()
                .map(PointLogDto.Response::from)
                .toList();
        return ResponseEntity.ok(pointLogs);
    }

    @PatchMapping("{id}/charge")
    public ResponseEntity<PointDto.Response> charge(
            @PathVariable long id,
            @RequestBody PointDto.Request request
    ) {
        Point charge = pointChargingService.charge(id, request.getAmount());
        return ResponseEntity.ok(PointDto.Response.from(charge));
    }

    @PatchMapping("{id}/use")
    public ResponseEntity<PointDto.Response> use(
            @PathVariable long id,
            @RequestBody PointDto.Request request
    ) {
        Point use = pointUsageService.use(id, request.getAmount());
        return ResponseEntity.ok(PointDto.Response.from(use));
    }
}
