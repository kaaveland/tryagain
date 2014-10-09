package com.github.kaaveland.tryagain;

public class WrappedException extends RuntimeException {
    public WrappedException(final Throwable cause) {
        super(cause);
    }
}
