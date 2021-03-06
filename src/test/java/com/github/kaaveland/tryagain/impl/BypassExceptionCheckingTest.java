package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.api.ExceptionMatcher;
import com.github.kaaveland.tryagain.api.Retriable;
import com.github.kaaveland.tryagain.api.RetriableWithoutResult;
import com.github.kaaveland.tryagain.api.Retrier;
import com.github.kaaveland.tryagain.api.TryAgain;
import org.junit.Test;

import java.io.IOException;

public class BypassExceptionCheckingTest {

    private class ThrowIOException implements RetriableWithoutResult {
        @Override
        public void execute(final int attempt) throws Exception {
            throw new IOException();
        }
    }

    private class ThrowIOExceptionWithResult implements Retriable<String> {
        @Override
        public String execute(final int attempt) throws Exception {
            throw new IOException();
        }
    }

    private Retrier retrier = TryAgain.retryOn(new ExceptionMatcher() {
        @Override
        public boolean retry(final Exception exception) {
            return true;
        }
    });

    @Test(expected = IOException.class)
    public void that_bypass_exception_checking_allows_us_to_throw_checked_exceptions_unchecked() {
        BypassExceptionChecking bypassExceptionChecking = new BypassExceptionChecking(retrier);
        bypassExceptionChecking.execute(new ThrowIOException());
    }

    @Test(expected = IOException.class)
    public void that_bypass_exception_checking_allows_us_to_throw_checked_exceptions_unchecked_with_result() {
        BypassExceptionChecking bypassExceptionChecking = new BypassExceptionChecking(retrier);
        bypassExceptionChecking.execute(new ThrowIOExceptionWithResult());
    }
}
