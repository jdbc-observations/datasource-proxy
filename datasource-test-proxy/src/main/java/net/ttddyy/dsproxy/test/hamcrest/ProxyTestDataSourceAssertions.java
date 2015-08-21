package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueriesHolder;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.QueryHolder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSourceAssertions {

    public static Matcher<ProxyTestDataSource> executions(int index, ExecutionType executionType) {
        return executions(index, new ExecutionTypeMatcher(executionType));
    }

    public static Matcher<ProxyTestDataSource> executions(final int index, Matcher<? super QueryExecution> queryExecutionMatcher) {
        return new CompositeMatcher<ProxyTestDataSource, QueryExecution>(queryExecutionMatcher) {

            @Override
            protected boolean validateByThisMatcher(ProxyTestDataSource item, Description expected, Description actual) {
                List<QueryExecution> queryExecutions = item.getQueryExecutions();
                int size = queryExecutions.size();
                if (size - 1 < index) {
                    expected.appendText("queryExecutions[" + index + "] exists");
                    actual.appendText("queryExecutions[] size was " + size);
                    return false;
                }
                return true;
            }

            @Override
            public QueryExecution getValue(ProxyTestDataSource actual) {
                return actual.getQueryExecutions().get(index);
            }

            @Override
            public String getSubMatcherFailureDescriptionPrefix() {
                return "queryExecutions[" + index + "] ";
            }
        };
    }


    public static Matcher<ProxyTestDataSource> totalCount(final int count) {
        return new TypeSafeMatcher<ProxyTestDataSource>() {
            @Override
            protected boolean matchesSafely(ProxyTestDataSource item) {
                int actualSize = countQueries(item);
                return count == actualSize;
            }

            @Override
            public void describeTo(Description description) {
                // expected clause
                description.appendText(count + " query executions");
            }

            @Override
            protected void describeMismatchSafely(ProxyTestDataSource item, Description mismatchDescription) {
                // but was clause
                int actualSize = countQueries(item);
                mismatchDescription.appendText("was " + actualSize + " query executions");
            }

            private int countQueries(ProxyTestDataSource ds) {
                int count = 0;
                for (QueryExecution queryExecution : ds.getQueryExecutions()) {
                    if (queryExecution instanceof QueryHolder) {
                        count++;
                    } else if (queryExecution instanceof QueriesHolder) {
                        count += ((QueriesHolder) queryExecution).getQueries().size();
                    }
                }
                return count;
            }
        };
    }

    public static Matcher<ProxyTestDataSource> selectCount(final int count) {
        return new QueryTypeCountMatcher(QueryType.SELECT, count);
    }

    public static Matcher<ProxyTestDataSource> insertCount(final int count) {
        return new QueryTypeCountMatcher(QueryType.INSERT, count);
    }

    public static Matcher<ProxyTestDataSource> updateCount(final int count) {
        return new QueryTypeCountMatcher(QueryType.UPDATE, count);
    }

    public static Matcher<ProxyTestDataSource> deleteCount(final int count) {
        return new QueryTypeCountMatcher(QueryType.DELETE, count);
    }

    public static Matcher<ProxyTestDataSource> otherCount(final int count) {
        return new QueryTypeCountMatcher(QueryType.OTHER, count);
    }

    private static class QueryTypeCountMatcher extends TypeSafeMatcher<ProxyTestDataSource> {

        private QueryType expectedQueryType;
        private int matchedCount = 0;
        private int expectedCount = 0;

        private QueryTypeCountMatcher(QueryType expectedQueryType, int expectedCount) {
            this.expectedQueryType = expectedQueryType;
            this.expectedCount = expectedCount;
        }

        @Override
        protected boolean matchesSafely(ProxyTestDataSource item) {
            for (QueryExecution queryExecution : item.getQueryExecutions()) {
                if (queryExecution instanceof QueryHolder) {
                    String query = ((QueryHolder) queryExecution).getQuery();
                    QueryType queryType = QueryUtils.getQueryType(query);
                    if (this.expectedQueryType.equals(queryType)) {
                        this.matchedCount++;
                    }
                } else if (queryExecution instanceof QueriesHolder) {
                    // for StatementBatchExecution
                    for (String query : ((QueriesHolder) queryExecution).getQueries()) {
                        QueryType queryType = QueryUtils.getQueryType(query);
                        if (this.expectedQueryType.equals(queryType)) {
                            this.matchedCount++;
                        }
                    }
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
