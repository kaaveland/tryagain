package com.github.kaaveland.tryagain;

public interface DelayStrategy {
    public long delay(int attempt);
}
