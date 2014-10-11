package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.api.Retriable;
import com.github.kaaveland.tryagain.api.RetriableWithoutResult;
import com.github.kaaveland.tryagain.api.Retrier;
import com.github.kaaveland.tryagain.api.WrappedException;
import org.junit.Test;

import java.io.IOException;

import static com.github.kaaveland.tryagain.api.TryAgain.on;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class WrapExceptionsTest {

    private final Retrier retrier = on(Exception.class);
    private final Retriable<Integer> throwIOException = new Retriable<Integer>() {
        @Override
        public Integer execute(final int attempt) throws Exception {
            throw new IOException();
        }
    };
    private final RetriableWithoutResult throwIllegalArgumentException = new RetriableWithoutResult() {
        @Override
        public void execute(final int attempt) throws Exception {
            throw new IllegalArgumentException();
        }
    };

    @Test
    public void that_exceptions_are_thrown_wrapped_with_cause_being_the_original_exception() {
        try {
            new WrapExceptions(retrier).execute(throwIOException);
        } catch (WrappedException exception) {
            assertThat(exception.getCause(), instanceOf(IOException.class));
        }
    }

    @Test
    public void that_exceptions_are_thrown_wrapped_with_cause_being_the_original_exception_from_withoutresult() {
        try {
            new WrapExceptions(retrier).execute(throwIllegalArgumentException);
        } catch (WrappedException exception) {
            assertThat(exception.getCause(), instanceOf(IllegalArgumentException.class));
        }
    }
}
