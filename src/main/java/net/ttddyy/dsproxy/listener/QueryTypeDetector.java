package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryType;
import org.jspecify.annotations.Nullable;

/**
 * Classifies a query for query counting. The default is {@link QueryUtils#getQueryType(String)}, which looks at the
 * first keyword only (so {@code WITH ...} is {@link QueryType#OTHER}). Supply a dialect-aware detector with
 * {@code ProxyDataSourceBuilder.queryTypeDetector(...)}. Implementations must be thread-safe.
 *
 * @author Tadaya Tsuyukubo
 */
@FunctionalInterface
public interface QueryTypeDetector {

    /**
     * @param query the query as executed, possibly {@code null}
     * @return the query type, never {@code null}
     */
    QueryType detect(@Nullable String query);

}
