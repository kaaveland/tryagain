package com.github.kaaveland.tryagain.api;

import com.github.kaaveland.tryagain.impl.ExceptionIn;
import com.github.kaaveland.tryagain.impl.InstanceOf;

/**
 * TryAgain is a natural entrypoint to create Retrier in a readable way.
 *
 * Example usage:
 * <pre>
 *     {@code
 *     TryAgain.on(SocketTimeoutException.class, HttpStatusException.class)
 *      .maxAttempts(10).withDelay(1000)
 *      .execute(postFile);
 *
 *     return TryAgain.onInstanceOf(IOException.class)
 *      .maxAttempts(5)
 *      .execute(fetchUser);
 *     }
 * </pre>
 */
public class TryAgain {

    /**
     * Create a default Retrier with 1 attempt and no delay.
     * @param exceptionMatches
     * @return
     */
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
        return new Retrier(new InstanceOf(exceptions));
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
