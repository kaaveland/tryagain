package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.Retriable;
import com.github.kaaveland.tryagain.RetriableWithoutResult;
import com.github.kaaveland.tryagain.Retrier;
import com.github.kaaveland.tryagain.WrappedException;

public class WrapExceptions {
    public final Retrier retrier;

    public WrapExceptions(final Retrier retrier) {
        this.retrier = retrier;
    }

    public <T> T execute(Retriable<T> operation) {
        try {
            return retrier.execute(operation);
        } catch (Exception exception) {
            throw new WrappedException(exception);
        }
    }

    public void execute(RetriableWithoutResult operation) {
        try {
            retrier.execute(operation);
        } catch (Exception exception) {
            throw new WrappedException(exception);
        }
    }
}
