package com.github.kaaveland.tryagain.api;

/**
 * Used to configure Retrier and tell it which exceptions to retry.
 */
public interface ExceptionMatcher {
    /**
     * Decide whether or not to retry after this particular exception.
     * @param exception
     * @return
     */
    public boolean retry(Exception exception);
}
