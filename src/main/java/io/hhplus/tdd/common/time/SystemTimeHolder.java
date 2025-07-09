package io.hhplus.tdd.common.time;

import org.springframework.stereotype.Component;

@Component
public class SystemTimeHolder implements TimeHolder{

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
