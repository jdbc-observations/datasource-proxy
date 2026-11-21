package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * Thread-safe counters for one DataSource. Produces {@link QueryCount} snapshots.
 */
final class MutableQueryCount {

    private final LongAdder select = new LongAdder();
    private final LongAdder insert = new LongAdder();
    private final LongAdder update = new LongAdder();
    private final LongAdder delete = new LongAdder();
    private final LongAdder other = new LongAdder();
    private final LongAdder statement = new LongAdder();
    private final LongAdder prepared = new LongAdder();
    private final LongAdder callable = new LongAdder();
    private final LongAdder total = new LongAdder();
    private final LongAdder success = new LongAdder();
    private final LongAdder failure = new LongAdder();
    private final LongAdder timeNanos = new LongAdder();

    void record(QueryExecutionContext context, List<QueryType> types) {
        this.total.increment();
        (context.isSuccess() ? this.success : this.failure).increment();
        this.timeNanos.add(context.getElapsedTime().toNanos());
        StatementType statementType = context.getStatementType();
        if (statementType != null) {
            switch (statementType) {
                case STATEMENT -> this.statement.increment();
                case PREPARED -> this.prepared.increment();
                case CALLABLE -> this.callable.increment();
            }
        }
        for (QueryType type : types) {
            switch (type) {
                case SELECT -> this.select.increment();
                case INSERT -> this.insert.increment();
                case UPDATE -> this.update.increment();
                case DELETE -> this.delete.increment();
                case OTHER -> this.other.increment();
            }
        }
    }

    QueryCount snapshot() {
        return new QueryCount(this.select.sum(), this.insert.sum(), this.update.sum(), this.delete.sum(),
                this.other.sum(), this.statement.sum(), this.prepared.sum(), this.callable.sum(), this.total.sum(),
                this.success.sum(), this.failure.sum(), Duration.ofNanos(this.timeNanos.sum()));
    }

}
