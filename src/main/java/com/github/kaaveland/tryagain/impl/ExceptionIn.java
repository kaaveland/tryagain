package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.api.ExceptionMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public class ExceptionIn implements ExceptionMatcher {

    private final Set<Class<? extends Exception>> exceptionTypes;

    public ExceptionIn(final Set<Class<? extends Exception>> exceptionTypes) {
        this.exceptionTypes = unmodifiableSet(exceptionTypes);
    }

    @SafeVarargs
    public ExceptionIn(Class<? extends Exception> ... exceptionTypes) {
        this(new HashSet<>(Arrays.asList(exceptionTypes)));
    }

    @Override
    public boolean matches(final Exception exception) {
        return exceptionTypes.contains(exception.getClass());
    }
}
