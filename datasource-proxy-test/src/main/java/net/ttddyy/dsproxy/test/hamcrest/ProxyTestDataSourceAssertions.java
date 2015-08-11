package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSourceAssertions {

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

    public static Matcher<ProxyTestDataSource> firstStatement(Matcher<StatementExecution> statementMatcher) {
        String msg = "first statement";  // TODO: check message
        return new FeatureMatcher<ProxyTestDataSource, StatementExecution>(statementMatcher, msg, msg) {
            @Override
            protected StatementExecution featureValueOf(ProxyTestDataSource actual) {
                List<QueryExecution> queryExecutions = actual.getQueryExecutions();
                QueryExecution queryExecution = findFirstByType(queryExecutions, StatementExecution.class);
                // TODO: if not exist
                return (StatementExecution) queryExecution;
            }
        };
    }

    public static Matcher<ProxyTestDataSource> firstBatchStatement(Matcher<StatementBatchExecution> statementBatchMatcher) {
        String msg = "first batch statement";  // TODO: check message
        return new FeatureMatcher<ProxyTestDataSource, StatementBatchExecution>(statementBatchMatcher, msg, msg) {
            @Override
            protected StatementBatchExecution featureValueOf(ProxyTestDataSource actual) {
                List<QueryExecution> queryExecutions = actual.getQueryExecutions();
                QueryExecution queryExecution = findFirstByType(queryExecutions, StatementBatchExecution.class);
                // TODO: if not exist
                return (StatementBatchExecution) queryExecution;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T extends QueryExecution> T findFirstByType(List<QueryExecution> executions, Class<T> type) {
        for (QueryExecution execution : executions) {
            if (type.isAssignableFrom(execution.getClass())) {
                return (T) execution;
            }
        }
        return null;
    }

    public static Matcher<ProxyTestDataSource> executions(int index, ExecutionType executionType) {
        return executions(index, new ExecutionTypeMatcher(executionType));
    }

    public static Matcher<ProxyTestDataSource> executions(final int index, Matcher<? super QueryExecution> queryExecutionMatcher) {
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


    public static Matcher<ProxyTestDataSource> totalCount(final int count) {
        return new TypeSafeMatcher<ProxyTestDataSource>() {
            @Override
            protected boolean matchesSafely(ProxyTestDataSource item) {
                return item.getQueryExecutions().size() == count;
            }

            @Override
            public void describeTo(Description description) {
                // expected clause
                description.appendText(count + " query executions");
            }

            @Override
            protected void describeMismatchSafely(ProxyTestDataSource item, Description mismatchDescription) {
                // but was clause
                int actualSize = item.getQueryExecutions().size();
                mismatchDescription.appendText("was " + actualSize + " query executions");
            }
        };
    }

    public static Matcher<ProxyTestDataSource> selectCount(final int count) {
        return new QueryTypeMatcher(QueryType.SELECT, count);
    }

    public static Matcher<ProxyTestDataSource> insertCount(final int count) {
        return new QueryTypeMatcher(QueryType.INSERT, count);
    }

    public static Matcher<ProxyTestDataSource> updateCount(final int count) {
        return new QueryTypeMatcher(QueryType.UPDATE, count);
    }

    public static Matcher<ProxyTestDataSource> deleteCount(final int count) {
        return new QueryTypeMatcher(QueryType.DELETE, count);
    }

    public static Matcher<ProxyTestDataSource> otherCount(final int count) {
        return new QueryTypeMatcher(QueryType.OTHER, count);
    }

    private static class QueryTypeMatcher extends TypeSafeMatcher<ProxyTestDataSource> {

        private QueryType expectedQueryType;
        private int matchedCount = 0;
        private int expectedCount = 0;

        private QueryTypeMatcher(QueryType expectedQueryType, int expectedCount) {
            this.expectedQueryType = expectedQueryType;
            this.expectedCount = expectedCount;
        }

        @Override
        protected boolean matchesSafely(ProxyTestDataSource item) {
            for (QueryExecution queryExecution : item.getQueryExecutions()) {
                if (this.expectedQueryType.equals(queryExecution.getQueryType())) {
                    this.matchedCount++;
                }
            }
            return this.matchedCount == this.expectedCount;
        }

        @Override
        public void describeTo(Description description) {
            // expected clause
            String msg = this.expectedCount + " " + this.expectedQueryType + " query executions";
            description.appendText(msg);
        }

        @Override
        protected void describeMismatchSafely(ProxyTestDataSource item, Description mismatchDescription) {
            // but was clause
            String msg = "was " + this.matchedCount + " " + this.expectedQueryType + " query executions";
            mismatchDescription.appendText(msg);
        }

    }

}
