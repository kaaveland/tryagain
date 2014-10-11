package com.github.kaaveland.tryagain.api;

import com.github.kaaveland.tryagain.impl.BypassExceptionChecking;
import com.github.kaaveland.tryagain.impl.ExceptionIn;
import com.github.kaaveland.tryagain.impl.ExponentialBackoffStrategy;
import com.github.kaaveland.tryagain.impl.InstancesOf;
import com.github.kaaveland.tryagain.impl.StaticDelayStrategy;
import com.github.kaaveland.tryagain.impl.WrapExceptions;

/**
 * Retrier is the entry point for tryagain that exposes the core functionality of the library.
 *
 * It is an immutable object, the methods used to reconfigure it return a new instance.
 */
public class Retrier {
    /**
     * The max amounts of attempts to invoke a Retriable before giving up.
     */
    public final int maxAttempts;
    /**
     * The delay strategy decides how much time to delay between subsequent attempts to invoke a Retriable.
     */
    public final DelayStrategy delayStrategy;
    /**
     * The exceptionMatcher decides whether to retry on a certain exception or to rethrow it.
     */
    public final ExceptionMatcher exceptionMatcher;

    public static Retrier retryOn(ExceptionMatcher exceptionMatches) {
        return new Retrier(exceptionMatches);
    }

    /**
     * This creates a Retrier with an ExceptionMatcher that matches exactly on exception class.
     *
     * This does not do instance-checking, so subclasses of exceptions are not retried.
     *
     * @param exceptions exception-classes to retry
     * @return A Retrier configured with an ExceptionIn-matcher.
     */
    @SafeVarargs
    public static Retrier on(Class<? extends Exception>... exceptions) {
        return new Retrier(new ExceptionIn(exceptions));
    }

    /**
     * This creates a Retrier with an ExceptionMatcher that does checks with Class.instanceOf.
     * @param exceptions exception classes to instance-check against
     * @return A Retrier configured with an InstanceOf exception-matcher
     */
    @SafeVarargs
    public static Retrier onInstanceOf(Class<? extends Exception>... exceptions) {
        return new Retrier(new InstancesOf(exceptions));
    }

    /**
     * A default Retrier that will invoke a retriable only once and use no delay between invocations.
     * @param exceptionMatcher decides which exceptions to retry.
     */
    public Retrier(ExceptionMatcher exceptionMatcher) {
        this(exceptionMatcher, 1, new DelayStrategy() {
            @Override
            public long delay(final int attempt) {
                return 0;
            }
        });
    }

    /**
     * Primary constructor
     * @param exceptionMatcher The exceptionMatcher decides whether to retry on a certain exception or to rethrow it.
     * @param maxAttempts The max amounts of attempts to invoke a Retriable before giving up.
     * @param delayStrategy The delay strategy decides how much time to delay between subsequent attempts to invoke a Retriable.
     */
    public Retrier(ExceptionMatcher exceptionMatcher, int maxAttempts, DelayStrategy delayStrategy) {
        this.exceptionMatcher = exceptionMatcher;
        this.maxAttempts = maxAttempts;
        this.delayStrategy = delayStrategy;
    }

    /**
     * @param maxAttempts
     * @return A new Retrier with a different value for maxAttempts.
     */
    public Retrier maxAttempts(int maxAttempts) {
        return new Retrier(exceptionMatcher, maxAttempts, delayStrategy);
    }

    /**
     * Use a constant delay between retries.
     * @param delay milliseconds to delay between retries.
     * @return A new Retrier with a constant delay between retries.
     */
    public Retrier withDelay(long delay) {
        return new Retrier(exceptionMatcher, maxAttempts, new StaticDelayStrategy(delay));
    }

    /**
     * Use an increasing delay between retries.
     * @param firstDelay milliseconds to delay after the first failed attempt. Subsequent attempts get firstDelay ^ attemptNumber millis.
     * @return A new Retrier with an increasing delay between attempts.
     */
    public Retrier exponentialBackoff(int firstDelay) {
        return new Retrier(exceptionMatcher, maxAttempts, new ExponentialBackoffStrategy(firstDelay));
    }

    /**
     * Wrap all exceptions for Retriables in WrappedException.
     * @return A new WrapExceptions delegating invocations of Retriable to this Retrier and wrapping exceptions.
     */
    public WrapExceptions wrapExceptions() {
        return new WrapExceptions(this);
    }

    /**
     * Rethrow all exceptions without having to have throws-declarations.
     * @return A BypassExceptionChecking delegating invocations of Retriable to this Retrier.
     */
    public BypassExceptionChecking bypassExceptionChecking() {
        return new BypassExceptionChecking(this);
    }

    /**
     * Execute RetriableWithoutResult up to maxAttempt times, delaying specified time between each attempt.
     * @param operation
     * @throws Exception
     */
    public void execute(RetriableWithoutResult operation) throws Exception {
        execute(from(operation));
    }

    /**
     * Execute Retriable up to maxAttempt times, delaying specified time between each attempt.
     * @param operation
     * @param <T> The type of the Retriable
     * @return The result of Retriable.execute.
     * @throws Exception
     */
    public <T> T execute(Retriable<T> operation) throws Exception {
        for (int attempt = 1; attempt < maxAttempts; attempt++) {
            try {
                return operation.execute(attempt);
            } catch (Exception exception) {
                if (!exceptionMatcher.retry(exception)) {
                    throw exception;
                }
                delay(attempt);
            }
        }
        return operation.execute(maxAttempts);
    }

    private void delay(int attempt) throws InterruptedException {
        Thread.sleep(delayStrategy.delay(attempt));
    }

    /**
     * Translate a RetriableWithoutResult to a Retriable of Void.
     * @param withoutResult
     * @return
     */
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
