package io.hhplus.tdd.point.domain.event;

import io.hhplus.tdd.point.domain.model.Point;
import io.hhplus.tdd.point.domain.model.TransactionType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PointLogEvent extends ApplicationEvent {

    private Point point;
    private TransactionType transactionType;

    public PointLogEvent(Object source, Point point, TransactionType transactionType) {
        super(source);
        this.point = point;
        this.transactionType = transactionType;
    }
}
