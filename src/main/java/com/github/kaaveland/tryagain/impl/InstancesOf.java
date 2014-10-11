package com.github.kaaveland.tryagain.impl;

import com.github.kaaveland.tryagain.ExceptionMatcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InstancesOf implements ExceptionMatcher {
    private final List<Class<? extends Exception>> exceptionTypes;

    public InstancesOf(final List<Class<? extends Exception>> exceptionTypes) {
        this.exceptionTypes = Collections.unmodifiableList(exceptionTypes);
    }

    @SafeVarargs
    public InstancesOf(Class<? extends Exception>... exceptionTypes) {
        this(Arrays.asList(exceptionTypes));
    }

    @Override
    public boolean matches(final Exception exception) {
        for (Class<? extends Exception> exceptionType : exceptionTypes) {
            if (exceptionType.isInstance(exception)) {
                return true;
            }
        }
        return false;
    }

}
