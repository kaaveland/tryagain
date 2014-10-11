package com.github.kaaveland.tryagain.api;

/**
 * Something that might fail by throwing an exception. Instanciate this to run code in a Retrier.
 * @param <T>
 */
public interface Retriable<T> {
    /**
     * @param attempt set to 1 for the first attempt, goes up to maxAttempts.
     * @return a T
     * @throws Exception
     */
    public T execute(int attempt) throws Exception;
}
