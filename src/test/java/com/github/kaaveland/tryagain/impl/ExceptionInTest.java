package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.ExceptionMatcher;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExceptionInTest {
    private final ExceptionMatcher runtimeAndIOException = new ExceptionIn(RuntimeException.class, IOException.class);

    @Test
    public void that_exception_in_matches_given_exceptions() {
        assertThat(runtimeAndIOException.matches(new RuntimeException()), is(true));
        assertThat(runtimeAndIOException.matches(new IOException()), is(true));
    }

    @Test
    public void that_exception_in_matches_exactly_and_not_subclasses() {
        assertThat(runtimeAndIOException.matches(new IllegalArgumentException()), is(false));
    }

}
