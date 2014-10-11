package com.github.kaaveland.tryagain.api;

/**
 * Used by WrapExceptions to enable removal 'throws'-declarations.
 */
public class WrappedException extends RuntimeException {
    public WrappedException(final Throwable cause) {
        super(cause);
    }
}
