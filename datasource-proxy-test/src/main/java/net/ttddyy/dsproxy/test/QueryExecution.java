package net.ttddyy.dsproxy.test;

import net.ttddyy.dsproxy.QueryType;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public interface QueryExecution {

    boolean isSuccess();

    boolean isBatch();

    QueryType getQueryType();
}
