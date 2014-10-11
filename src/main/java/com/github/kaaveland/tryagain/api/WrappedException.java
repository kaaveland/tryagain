package com.github.kaaveland.tryagain.api;

public class WrappedException extends RuntimeException {
    public WrappedException(final Throwable cause) {
        super(cause);
    }
}
