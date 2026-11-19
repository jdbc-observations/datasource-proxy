package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;

/**
 * Functional interface to be called for filtering queries to be logged.
 *
 * @see AbstractQueryLoggingListener
 * @since 1.11
 */
// TODO: add @FunctionalInterface once codebase is java8
public interface LoggingFilter {

    /**
     * Default filter that allows all queries to be logged.
     */
    LoggingFilter ALLOW_ALL = new LoggingFilter() {
        public boolean shouldLog(QueryExecutionContext context) {
            return true;
        }
    };

    /**
     * Determines if the query should be logged.
     *
     * @param context query execution context
     * @return true to log, false to skip
     */
    boolean shouldLog(QueryExecutionContext context);
}
