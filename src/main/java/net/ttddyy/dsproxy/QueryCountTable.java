package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Query counts by DataSource name. Shared by {@link QueryCounter.Scope} and {@link DataSourceQueryCounter}.
 */
final class QueryCountTable {

    private final Map<String, MutableQueryCount> counts = new ConcurrentHashMap<>();

    void record(@Nullable String dataSourceName, QueryExecutionContext context, List<QueryType> types) {
        this.counts.computeIfAbsent(Objects.requireNonNullElse(dataSourceName, ""), name -> new MutableQueryCount())
                .record(context, types);
    }

    QueryCount total() {
        return QueryCount.sum(this.counts.values().stream().map(MutableQueryCount::snapshot).toList());
    }

    QueryCount get(String dataSourceName) {
        MutableQueryCount count = this.counts.get(dataSourceName);
        return count == null ? QueryCount.ZERO : count.snapshot();
    }

    void reset() {
        this.counts.clear();
    }

}
