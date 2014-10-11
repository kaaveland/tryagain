package com.github.kaaveland.tryagain.api;

import com.github.kaaveland.tryagain.api.RetriableWithoutResult;
import com.github.kaaveland.tryagain.api.Retrier;
import com.github.kaaveland.tryagain.api.WrappedException;
import org.junit.Test;

import java.io.IOException;

import static com.github.kaaveland.tryagain.api.Retrier.on;
import static com.github.kaaveland.tryagain.api.Retrier.onInstanceOf;
import static junit.framework.Assert.fail;
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
}
