package com.github.kaaveland.tryagain.api;

import com.github.kaaveland.tryagain.impl.BypassExceptionChecking;
import com.github.kaaveland.tryagain.impl.ExceptionIn;
import com.github.kaaveland.tryagain.impl.ExponentialBackoffStrategy;
import com.github.kaaveland.tryagain.impl.InstancesOf;
import com.github.kaaveland.tryagain.impl.StaticDelayStrategy;
import com.github.kaaveland.tryagain.impl.WrapExceptions;

public class Retrier {
    public final int times;
    public final DelayStrategy delayStrategy;
    public final ExceptionMatcher exceptionMatcher;

    public static Retrier retryOn(ExceptionMatcher exceptionMatches) {
        return new Retrier(exceptionMatches);
    }

    @SafeVarargs
    public static Retrier on(Class<? extends Exception>... exceptions) {
        return new Retrier(new ExceptionIn(exceptions));
    }

    @SafeVarargs
    public static Retrier onInstanceOf(Class<? extends Exception>... exceptions) {
        return new Retrier(new InstancesOf(exceptions));
    }

    public Retrier(ExceptionMatcher exceptionMatcher) {
        this(exceptionMatcher, 1, new DelayStrategy() {
            @Override
            public long delay(final int attempt) {
                return 0;
            }
        });
    }

    public Retrier(ExceptionMatcher exceptionMatcher, int times, DelayStrategy delayStrategy) {
        this.exceptionMatcher = exceptionMatcher;
        this.times = times;
        this.delayStrategy = delayStrategy;
    }

    public Retrier maxAttempts(int times) {
        return new Retrier(exceptionMatcher, times, delayStrategy);
    }

    public Retrier withDelay(long delay) {
        return new Retrier(exceptionMatcher, times, new StaticDelayStrategy(delay));
    }

    public Retrier exponentialBackoff(int firstDelay) {
        return new Retrier(exceptionMatcher, times, new ExponentialBackoffStrategy(firstDelay));
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
        for (int attempt = 1; attempt < times; attempt++) {
            try {
                return operation.execute(attempt);
            } catch (Exception exception) {
                if (!exceptionMatcher.matches(exception)) {
                    throw exception;
                }
                delay(attempt);
            }
        }
        return operation.execute(times);
    }

    private void delay(int attempt) throws InterruptedException {
        Thread.sleep(delayStrategy.delay(attempt));
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
