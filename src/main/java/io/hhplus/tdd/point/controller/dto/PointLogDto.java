package io.hhplus.tdd.point.controller.dto;

import io.hhplus.tdd.point.domain.model.PointLog;
import io.hhplus.tdd.point.domain.model.TransactionType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PointLogDto {

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private Long userId;
        private Long point;
        private TransactionType type;
        private Long updateMillis;

        @Builder
        public Response(Long updateMillis, TransactionType type, Long point, Long userId, Long id) {
            this.updateMillis = updateMillis;
            this.type = type;
            this.point = point;
            this.userId = userId;
            this.id = id;
        }

        public static Response from(PointLog pointLog) {
            return Response.builder()
                    .id(pointLog.getId())
                    .userId(pointLog.getUserId())
                    .point(pointLog.getPoint())
                    .type(pointLog.getType())
                    .updateMillis(pointLog.getUpdateMillis())
                    .build();
        }
    }
}
