package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueryExecution;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSourceMatcher {

    // TODO: create ExecutionTypeMatcher:  executions(0, is(batch())) && executions(0, batch())

    // assertThat(ds, executions(0, batch()));
    // assertThat(ds, executions(0, is(batch())));
    // assertThat(ds, executions(0, statement()));
    // assertThat(ds, executions(0, batchStatement()));
    // assertThat(ds, executions(0, statementOrBatchStatement()));
    // assertThat(ds, executions(0, prepared()));
    // assertThat(ds, executions(0, batchPrepared()));
    // assertThat(ds, executions(0, preparedOrBatchPrepared()));
    // assertThat(ds, executions(0, callable()));
    // assertThat(ds, executions(0, batchCallable()));
    // assertThat(ds, executions(0, preparedOrBatchCallable()));
    // assertThat(ds, executions(0, is(success()))));
    // assertThat(ds, firstStatement(query(...)));
    // assertThat(ds, firstBatchStatement(query(...)));

    public static Matcher<ProxyTestDataSource> executions(int index, ExecutionType executionType) {
        return executions(index, new ExecutionTypeMatcher(executionType));
    }

    public static Matcher<ProxyTestDataSource> executions(final int index, Matcher<QueryExecution> queryExecutionMatcher) {
        // TODO: check message
        String msg = "queryExecutions[" + index + "]";
        return new FeatureMatcher<ProxyTestDataSource, QueryExecution>(queryExecutionMatcher, msg, msg) {
            @Override
            protected QueryExecution featureValueOf(ProxyTestDataSource actual) {
                List<QueryExecution> queryExecutions = actual.getQueryExecutions();
                // TODO: list size check, and use try-catch
                QueryExecution queryExecution = queryExecutions.get(index);
                return queryExecution;
            }
        };
    }


}
