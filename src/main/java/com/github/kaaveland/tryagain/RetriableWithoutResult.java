package com.github.kaaveland.tryagain;

public interface RetriableWithoutResult {
    public void execute(int attempt) throws Exception;
}
