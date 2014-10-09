package com.github.kaaveland.tryagain;

public class BypassExceptionChecking {
    private final Retrier retrier;

    public BypassExceptionChecking(final Retrier retrier) {
        this.retrier = retrier;
    }

    public <T> T execute(Retriable<T> operation) {
        try {
            return retrier.execute(operation);
        } catch (Exception exception) {
            ThrowUncheckedHack.throwUnchecked(exception);
            return null; // Dead code
        }
    }

    public void execute(RetriableWithoutResult operation) {
        try {
            retrier.execute(operation);
        } catch (Exception exception) {
            ThrowUncheckedHack.throwUnchecked(exception);
        }
    }

    public <E extends Exception> BypassExceptionChecking declare(Class<E> throwsE) throws E {
        return this;
    }
}
