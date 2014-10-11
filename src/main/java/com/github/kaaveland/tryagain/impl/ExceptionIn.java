package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.ExceptionMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ExceptionIn implements ExceptionMatcher {

    private final Set<Class<? extends Exception>> exceptionTypes;

    public ExceptionIn(final Set<Class<? extends Exception>> exceptionTypes) {
        this.exceptionTypes = exceptionTypes;
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
