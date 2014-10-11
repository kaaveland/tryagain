package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.api.ExceptionMatcher;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InstanceOfTest {
    ExceptionMatcher runtimeException = new InstanceOf(RuntimeException.class);

    @Test
    public void that_illegal_argument_exception_matches_instances_of_runtime_exception() {
        assertThat(runtimeException.retry(new IllegalArgumentException()), is(true));
    }

    @Test
    public void that_ioexception_does_not_match_instancesof_runtime_exception() {
        assertThat(runtimeException.retry(new IOException()), is(false));
    }
}
