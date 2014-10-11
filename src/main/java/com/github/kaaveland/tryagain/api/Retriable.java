package com.github.kaaveland.tryagain.api;

public interface Retriable<T> {
    public T execute(int attempt) throws Exception;
}
