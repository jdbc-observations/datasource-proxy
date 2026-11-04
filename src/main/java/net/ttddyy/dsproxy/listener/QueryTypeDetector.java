package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryType;

/**
 * Determines the query type used by {@link DataSourceQueryCountListener}.
 *
 * <p>Implementations can provide database-specific classification without changing how query counts are stored.
 * An implementation must be thread-safe if its listener is shared across threads.
 */
public interface QueryTypeDetector {

    /**
     * Determines the type of the original query, before comment or whitespace removal.
     *
     * @param query a query string, possibly {@code null}
     * @return the query type, never {@code null}
     */
    QueryType getQueryType(String query);

}
