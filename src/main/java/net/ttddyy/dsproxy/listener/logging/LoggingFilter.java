package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

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
        public boolean shouldLog(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
            return true;
        }
    };

    /**
     * Determines if the query should be logged.
     *
     * @param execInfo execution context
     * @param queryInfoList list of queries
     * @return true to log, false to skip
     */
    boolean shouldLog(ExecutionInfo execInfo, List<QueryInfo> queryInfoList);
}
