package com.github.kaaveland.tryagain;

import org.junit.Test;

import java.io.IOException;

public class BypassExceptionCheckingTest {

    private class ThrowIOException implements RetriableWithoutResult {
        @Override
        public void execute(final int attempt) throws Exception {
            throw new IOException();
        }
    }

    private Retrier retrier = Retrier.retryOn(new ExceptionMatcher() {
        @Override
        public boolean matches(final Exception exception) {
            return true;
        }
    });

    @Test(expected = IOException.class)
    public void that_bypass_exception_checking_allows_us_to_throw_checked_exceptions_unchecked() {
        BypassExceptionChecking bypassExceptionChecking = new BypassExceptionChecking(retrier);
        bypassExceptionChecking.execute(new ThrowIOException());
    }

}
