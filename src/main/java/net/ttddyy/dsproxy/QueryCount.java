package net.ttddyy.dsproxy;

import java.time.Duration;
import java.util.Collection;

/**
 * Immutable snapshot of query metrics.
 *
 * @param select    number of SELECT queries
 * @param insert    number of INSERT queries
 * @param update    number of UPDATE queries
 * @param delete    number of DELETE queries
 * @param other     number of other queries
 * @param statement number of executions through {@link java.sql.Statement}
 * @param prepared  number of executions through {@link java.sql.PreparedStatement}
 * @param callable  number of executions through {@link java.sql.CallableStatement}
 * @param total     number of executions (a batch counts once)
 * @param success   number of successful executions
 * @param failure   number of failed executions
 * @param time      total execution time
 * @author Tadaya Tsuyukubo
 * @see QueryCounter
 * @see DataSourceQueryCounter
 */
public record QueryCount(long select, long insert, long update, long delete, long other,
                         long statement, long prepared, long callable,
                         long total, long success, long failure, Duration time) {

    static final QueryCount ZERO = new QueryCount(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Duration.ZERO);

    static QueryCount sum(Collection<QueryCount> counts) {
        QueryCount result = ZERO;
        for (QueryCount c : counts) {
            result = new QueryCount(result.select + c.select, result.insert + c.insert, result.update + c.update,
                    result.delete + c.delete, result.other + c.other, result.statement + c.statement,
                    result.prepared + c.prepared, result.callable + c.callable, result.total + c.total,
                    result.success + c.success, result.failure + c.failure, result.time.plus(c.time));
        }
        return result;
    }

}
