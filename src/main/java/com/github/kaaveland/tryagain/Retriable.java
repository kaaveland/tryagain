package com.github.kaaveland.tryagain;

public interface Retriable<T> {
    public T execute(int attempt) throws Exception;
}
