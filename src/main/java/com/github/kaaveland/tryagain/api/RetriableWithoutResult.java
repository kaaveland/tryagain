package com.github.kaaveland.tryagain.api;

/**
 * A Retriable operiation that is called for its side effect and does not need to return anything.
 */
public interface RetriableWithoutResult {
    /**
     *
     * @param attempt set to 1 for the first attempt, goes up to maxAttempts.
     * @throws Exception
     */
    public void execute(int attempt) throws Exception;
}
