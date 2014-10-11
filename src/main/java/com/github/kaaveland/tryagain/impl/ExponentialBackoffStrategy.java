package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.api.DelayStrategy;

public class ExponentialBackoffStrategy implements DelayStrategy {
    private final int baseBackoff;

    public ExponentialBackoffStrategy(final int baseBackoff) {
        this.baseBackoff = baseBackoff;
    }

    @Override
    public long delay(final int attempt) {
        return (long) Math.pow(baseBackoff, attempt);
    }
}
