package com.github.kaaveland.tryagain;

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
