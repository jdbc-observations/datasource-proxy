package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Counts every query on the DataSources it is registered with, from any thread.
 *
 * <pre>{@code
 * var counter = new DataSourceQueryCounter();
 * DataSource ds = ProxyDataSourceBuilder.create(actual).countQuery(counter).build();
 * counter.snapshot().select();
 * counter.reset();
 * }</pre>
 *
 * <p>A snapshot taken while queries are running is not atomic: it may include part of a query's counts.
 *
 * @author Tadaya Tsuyukubo
 * @see QueryCounter
 */
public final class DataSourceQueryCounter {

    private final QueryCountTable table = new QueryCountTable();

    /**
     * @return grand total across all DataSources
     */
    public QueryCount snapshot() {
        return this.table.total();
    }

    /**
     * @param dataSourceName DataSource name; {@code ""} for a DataSource without a name
     * @return counts for that DataSource; all zero if it ran nothing
     */
    public QueryCount snapshot(String dataSourceName) {
        return this.table.get(dataSourceName);
    }

    /**
     * Clear all counts.
     */
    public void reset() {
        this.table.reset();
    }

    void record(@Nullable String dataSourceName, QueryExecutionContext context, List<QueryType> types) {
        this.table.record(dataSourceName, context, types);
    }

}
