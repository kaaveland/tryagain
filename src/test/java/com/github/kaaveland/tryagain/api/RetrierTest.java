package com.github.kaaveland.tryagain.api;

import com.github.kaaveland.tryagain.impl.ExceptionIn;
import org.junit.Test;

import java.io.IOException;

import static com.github.kaaveland.tryagain.api.Retrier.on;
import static com.github.kaaveland.tryagain.api.Retrier.onInstanceOf;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class RetrierTest {

    @Test
    public void that_translated_retriablewithoutresult_invokes_source_once() throws Exception {
        RetriableWithoutResult source = mock(RetriableWithoutResult.class);
        Retrier.from(source).execute(1);
        verify(source, times(1)).execute(1);
    }

    @Test
    public void that_execute_invokes_once_with_default_settings() throws Exception {
        RetriableWithoutResult operation = mock(RetriableWithoutResult.class);
        on(RuntimeException.class).execute(operation);
        verify(operation, times(1)).execute(1);
    }

    @Test
    public void that_execute_invokes_three_times_when_specified_and_swallows_retried_exceptions() throws Exception {
        RetriableWithoutResult operation = mock(RetriableWithoutResult.class);
        doThrow(new RuntimeException()).when(operation).execute(1);
        doThrow(new RuntimeException()).when(operation).execute(2);
        on(RuntimeException.class).maxAttempts(3).execute(operation);
        verify(operation, times(3)).execute(anyInt());
    }

    @Test(expected = IOException.class)
    public void that_unmatched_exceptions_pass_up_the_stack() throws Exception {
        RetriableWithoutResult operation = mock(RetriableWithoutResult.class);
        doThrow(new IOException()).when(operation).execute(1);
        on(RuntimeException.class).maxAttempts(3).execute(operation);
    }

    @Test
    public void that_attempts_are_done_in_order_from_1_to_max_attempt() throws Exception {
        RetriableWithoutResult operation = mock(RetriableWithoutResult.class);
        doThrow(new IOException()).when(operation).execute(anyInt());
        try {
            on(IOException.class).maxAttempts(4).execute(operation);
        } catch (IOException _) {} // Ignore
        verify(operation).execute(1);
        verify(operation).execute(2);
        verify(operation).execute(3);
        verify(operation).execute(4);
        verifyNoMoreInteractions(operation);
    }

    @Test(expected = WrappedException.class)
    public void that_wrapExceptions_causes_WrappedException_to_be_thrown_on_exception() {
        RetriableWithoutResult operation = mock(RetriableWithoutResult.class);
        try {
            doThrow(new IOException()).when(operation).execute(anyInt());
        } catch (Exception e) {
            fail("Shouldn't happen when configuring mock");
        }
        onInstanceOf(IOException.class).maxAttempts(3).wrapExceptions().execute(operation);
    }

    @Test(expected = WrappedException.class)
    public void that_wrapExceptions_causes_WrappedException_to_be_thrown_on_unmatched_exception() {
        RetriableWithoutResult operation = mock(RetriableWithoutResult.class);
        try {
            doThrow(new IOException()).when(operation).execute(anyInt());
        } catch (Exception e) {
            fail("Shouldn't happen when configuring mock");
        }
        on(RuntimeException.class).maxAttempts(3).wrapExceptions().execute(operation);
    }

    @Test(expected = IOException.class)
    public void that_bypassExceptionChecking_causes_checked_exceptions_to_be_thrown_without_declaration() {
        RetriableWithoutResult operation = mock(RetriableWithoutResult.class);
        try {
            doThrow(new IOException()).when(operation).execute(anyInt());
        } catch (Exception e) {
            fail("Shouldn't happen when configuring mock");
        }
        on(RuntimeException.class).bypassExceptionChecking().execute(operation);
    }

    @Test
    public void that_we_sleep_the_amount_of_time_specified_by_delayStrategy() {
        Retrier retrier = on(RuntimeException.class).maxAttempts(2).withDelay(1000);
        RetriableWithoutResult operation = mock(RetriableWithoutResult.class);
        try {
            doThrow(new RuntimeException()).when(operation).execute(1);
        } catch (Exception e) {
            fail("Shouldn't fail when setting up mock");
        }
        long before = System.currentTimeMillis();
        retrier.wrapExceptions().execute(operation);
        long elapsed = System.currentTimeMillis() - before;
        assertThat(elapsed, is(greaterThanOrEqualTo(1000L)));
        assertThat(elapsed, is(lessThan(1100L)));
    }

    @Test
    public void that_we_invoke_delayStrategy_with_attempts_in_order() {
        DelayStrategy delay = mock(DelayStrategy.class);
        Retrier retrier = new Retrier(new ExceptionIn(RuntimeException.class), 3, delay);
        RetriableWithoutResult operation = mock(RetriableWithoutResult.class);
        try {
            doThrow(new RuntimeException()).when(operation).execute(1);
            doThrow(new RuntimeException()).when(operation).execute(2);
        } catch (Exception e) {
            fail("Shouldn't fail when setting up mock");
        }
        retrier.wrapExceptions().execute(operation);
        verify(delay).delay(1);
        verify(delay).delay(2);
        verifyNoMoreInteractions(delay);
    }

    @Test
    public void that_retrier_returns_the_result_of_operation() {
        String result = on(RuntimeException.class).maxAttempts(2).wrapExceptions().execute(new Retriable<String>() {
            @Override
            public String execute(final int attempt) throws Exception {
                if (attempt == 1) {
                    throw new RuntimeException();
                }
                return "Result";
            }
        });
        assertThat(result, equalTo("Result"));
    }

}
