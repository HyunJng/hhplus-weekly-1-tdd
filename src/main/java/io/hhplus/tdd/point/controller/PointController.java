package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.controller.dto.PointDto;
import io.hhplus.tdd.point.controller.dto.PointLogDto;
import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;

    @GetMapping("{id}")
    public ResponseEntity<PointDto.Response> point(
            @PathVariable long id
    ) {
        Point point = pointService.findPoint(id);
        return ResponseEntity.ok(PointDto.Response.from(point));
    }

    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointLogDto.Response>> history(
            @PathVariable long id
    ) {
        List<PointLogDto.Response> pointLogs = pointService.findPointLogDesc(id).stream()
                .map(PointLogDto.Response::from)
                .toList();
        return ResponseEntity.ok(pointLogs);
    }

    @PatchMapping("{id}/charge")
    public ResponseEntity<PointDto.Response> charge(
            @PathVariable long id,
            @RequestBody PointDto.Request request
    ) {
        Point charge = pointService.charge(id, request.getAmount());
        return ResponseEntity.ok(PointDto.Response.from(charge));
    }

    @PatchMapping("{id}/use")
    public ResponseEntity<PointDto.Response> use(
            @PathVariable long id,
            @RequestBody PointDto.Request request
    ) {
        Point use = pointService.use(id, request.getAmount());
        return ResponseEntity.ok(PointDto.Response.from(use));
    }
}
