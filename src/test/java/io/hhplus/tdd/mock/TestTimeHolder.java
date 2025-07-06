package io.hhplus.tdd.mock;

import io.hhplus.tdd.common.time.TimeHolder;

public class TestTimeHolder implements TimeHolder {
    private final long now;

    public TestTimeHolder(long time) {
        this.now = time;
    }

    @Override
    public long currentTimeMillis() {
        return now;
    }
}
