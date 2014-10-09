package com.github.kaaveland.tryagain;

public class Retrier {
    public final int times;
    public final int delay;
    public final ExceptionMatcher exceptionMatcher;

    public static Retrier retryOn(ExceptionMatcher exceptionMatches) {
        return new Retrier(exceptionMatches);
    }

    public static Retrier retryOn(Class<? extends Exception> ...exceptions) {
        return retryOn(new ExceptionIn(exceptions));
    }

    public Retrier(ExceptionMatcher exceptionMatcher) {
        this(exceptionMatcher, 1, 0);
    }

    public Retrier(ExceptionMatcher exceptionMatcher, int times, int delay) {
        this.exceptionMatcher = exceptionMatcher;
        this.times = times;
        this.delay = delay;
    }

    public Retrier times(int times) {
        return new Retrier(exceptionMatcher, times, delay);
    }

    public Retrier delaying(int delay) {
        return new Retrier(exceptionMatcher, times, delay);
    }

    public WrapExceptions wrapExceptions() {
        return new WrapExceptions(this);
    }

    public BypassExceptionChecking bypassExceptionChecking() {
        return new BypassExceptionChecking(this);
    }

    public void execute(RetriableWithoutResult operation) throws Exception {
        execute(from(operation));
    }

    public <T> T execute(Retriable<T> operation) throws Exception {
        for (int attempt = 0; attempt < times; attempt++) {
            try {
                return operation.execute(attempt);
            } catch (Exception exception) {
                if (!exceptionMatcher.matches(exception)) {
                    throw exception;
                }
                delay();
            }
        }
        return operation.execute(times);
    }

    private void delay() throws InterruptedException {
        Thread.sleep(delay);
    }

    public static Retriable<Void> from(final RetriableWithoutResult withoutResult) {
        return new Retriable<Void>() {
            @Override
            public Void execute(final int attempt) throws Exception {
                withoutResult.execute(attempt);
                return null;
            }
        };
    }

}
