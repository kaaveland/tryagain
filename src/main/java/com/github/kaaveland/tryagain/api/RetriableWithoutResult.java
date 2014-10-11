package com.github.kaaveland.tryagain.api;

public interface RetriableWithoutResult {
    public void execute(int attempt) throws Exception;
}
