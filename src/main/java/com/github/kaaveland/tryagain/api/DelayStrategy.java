package com.github.kaaveland.tryagain.api;

public interface DelayStrategy {
    public long delay(int attempt);
}
