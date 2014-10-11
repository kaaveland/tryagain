package com.github.kaaveland.tryagain.api;

/**
 * Can be used to configure different ways of waiting between failing attempts in a Retrier.
 */
public interface DelayStrategy {
    /**
     * Calculate the amount of milliseconds to wait before attempting to do a new invocation.
     * @param attempt 1 if this is the first attempt, increases by 1 for every subsequent attempt.
     * @return the amount of millis to wait.
     */
    public long delay(int attempt);
}
