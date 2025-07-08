package io.hhplus.tdd.point.controller.dto;

import io.hhplus.tdd.point.domain.model.Point;
import lombok.*;

public class PointDto {

    @Data
    @NoArgsConstructor
    public static class Request {
        private Long amount;

        public Request(Long amount) {
            this.amount = amount;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Response {
        private Long userId;
        private Long point;
        private Long updateMillis;

        @Builder
        public Response(Long userId, Long point, Long updateMillis) {
            this.userId = userId;
            this.point = point;
            this.updateMillis = updateMillis;
        }

        public static Response from(Point point) {
            return Response.builder()
                    .userId(point.getUserId())
                    .point(point.getAmount())
                    .updateMillis(point.getUpdateMillis())
                    .build();
        }
    }
}
