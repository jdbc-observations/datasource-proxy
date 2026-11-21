package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.listener.QueryTypeDetector;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Records query counts. Added by {@code ProxyDataSourceBuilder.countQuery()}.
 *
 * @author Tadaya Tsuyukubo
 * @see DataSourceQueryCounter
 */
public final class QueryCountListener implements ProxyDataSourceListener {

    private final @Nullable DataSourceQueryCounter counter;

    private final QueryTypeDetector queryTypeDetector;

    public QueryCountListener(@Nullable DataSourceQueryCounter counter, QueryTypeDetector queryTypeDetector) {
        this.counter = counter;
        this.queryTypeDetector = queryTypeDetector;
    }

    @Override
    public void afterQuery(QueryExecutionContext context) {
        if (this.counter == null) {
            return;
        }
        List<QueryType> types = new ArrayList<>(context.getQueries().size());
        for (QueryInfo queryInfo : context.getQueries()) {
            String query = queryInfo.getQuery();
            types.add(Objects.requireNonNull(this.queryTypeDetector.detect(query),
                    () -> "QueryTypeDetector returned null for: " + query));
        }
        this.counter.record(context.getDataSourceName(), context, types);
    }

    public @Nullable DataSourceQueryCounter getCounter() {
        return this.counter;
    }

    public QueryTypeDetector getQueryTypeDetector() {
        return this.queryTypeDetector;
    }

}
