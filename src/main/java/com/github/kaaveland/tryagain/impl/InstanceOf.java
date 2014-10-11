package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.api.ExceptionMatcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This will match exceptions that are instances of any of the classes passed in to constructor.
 */
public class InstanceOf implements ExceptionMatcher {
    private final List<Class<? extends Exception>> exceptionTypes;

    public InstanceOf(final List<Class<? extends Exception>> exceptionTypes) {
        this.exceptionTypes = Collections.unmodifiableList(exceptionTypes);
    }

    @SafeVarargs
    public InstanceOf(Class<? extends Exception>... exceptionTypes) {
        this(Arrays.asList(exceptionTypes));
    }

    @Override
    public boolean retry(final Exception exception) {
        for (Class<? extends Exception> exceptionType : exceptionTypes) {
            if (exceptionType.isInstance(exception)) {
                return true;
            }
        }
        return false;
    }

}
