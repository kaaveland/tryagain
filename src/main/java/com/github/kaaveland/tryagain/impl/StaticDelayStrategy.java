package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.api.DelayStrategy;

public class StaticDelayStrategy implements DelayStrategy {
    private final long delay;

    public StaticDelayStrategy(final long delay) {
        this.delay = delay;
    }

    @Override
    public long delay(final int attempt) {
        return delay;
    }
}
