package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueriesHolder;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.QueryHolder;
import net.ttddyy.dsproxy.test.StatementExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

/**
 * Hamcrest matchers for {@link ProxyTestDataSource}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class ProxyTestDataSourceAssertions {

    /**
     * Matcher for {@link QueryExecution} of given index.
     *
     * Example:
     * <pre>
     * assertThat(ds, executions(0, IS_BATCH));
     * assertThat(ds, executions(0, IS_STATEMENT));
     * assertThat(ds, executions(0, IS_STATEMENT_OR_BATCH_STATEMENT));
     * </pre>
     */
    public static Matcher<ProxyTestDataSource> executions(int index, ExecutionType executionType) {
        return executions(index, new ExecutionTypeMatcher(executionType));
    }

    /**
     * Matcher for {@link QueryExecution} of given index.
     *
     * Example:
     * <pre>
     * assertThat(ds, executions(0, statement()));
     * assertThat(ds, executions(0, isPreparedOrBatchPrepared()));
     * assertThat(ds, executions(0, is(success())));
     * </pre>
     */
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


    /**
     * Matcher to check the number of {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, executionCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> executionCount(int count) {
        String msg = "queryExecutions size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getQueryExecutions().size();
            }
        };
    }

    /**
     * Matcher to check the number of {@link StatementExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, statementCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> statementCount(int count) {
        String msg = "StatementExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getStatements().size();
            }
        };
    }

    /**
     * Matcher to check the number of {@link StatementBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, batchStatementCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> batchStatementCount(int count) {
        String msg = "StatementBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getBatchStatements().size();
            }
        };
    }

    /**
     * Matcher to check the number of {@link StatementExecution} or {@link StatementBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, statementOrBatchStatementCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> statementOrBatchStatementCount(int count) {
        String msg = "StatementExecution or StatementBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getStatements().size() + actual.getBatchStatements().size();
            }
        };
    }

    /**
     * Matcher to check the number of {@link PreparedExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, preparedCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> preparedCount(int count) {
        String msg = "PreparedExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getPrepareds().size();
            }
        };
    }

    /**
     * Matcher to check the number of {@link PreparedBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, batchPreparedCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> batchPreparedCount(int count) {
        String msg = "PreparedBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getBatchPrepareds().size();
            }
        };
    }

    /**
     * Matcher to check the number of {@link PreparedExecution} or {@link PreparedBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, preparedOrBatchPreparedCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> preparedOrBatchPreparedCount(int count) {
        String msg = "PreparedExecution or PreparedBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getPrepareds().size() + actual.getBatchPrepareds().size();
            }
        };
    }

    /**
     * Matcher to check the number of {@link CallableExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, callableCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> callableCount(int count) {
        String msg = "CallableExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getCallables().size();
            }
        };

    }

    /**
     * Matcher to check the number of {@link CallableBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, batchCallableCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> batchCallableCount(int count) {
        String msg = "CallableBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getBatchCallables().size();
            }
        };

    }

    /**
     * Matcher to check the number of {@link CallableExecution} or {@link CallableBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, callableOrBatchCallableCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> callableOrBatchCallableCount(int count) {
        String msg = "CallableExecution or CallableBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getCallables().size() + actual.getBatchCallables().size();
            }
        };
    }


    /**
     * Matcher to check the number of queries in {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * This matcher counts the number of queries whereas {@link #executionCount(int)} counts the number of executions.
     * The number of queries and executions may differ when there is a Batch execution of {@link java.sql.Statement}.
     * Single execution of {@link java.sql.Statement} may contain multiple queries. Thus, {@link #executionCount(int)}
     * count it as one, but this matcher may report more than one queries.
     *
     * Example:
     * <pre> assertThat(ds, totalQueryCount(3)); </pre>
     *
     * @see #executionCount(int)
     */
    public static Matcher<ProxyTestDataSource> totalQueryCount(final int count) {
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

    /**
     * Matcher to check the number of SELECT queries in {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, selectCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> selectCount(final int count) {
        return new QueryTypeCountMatcher(QueryType.SELECT, count);
    }

    /**
     * Matcher to check the number of INSERT queries in {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, insertCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> insertCount(final int count) {
        return new QueryTypeCountMatcher(QueryType.INSERT, count);
    }

    /**
     * Matcher to check the number of UPDATE queries in {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, updateCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> updateCount(final int count) {
        return new QueryTypeCountMatcher(QueryType.UPDATE, count);
    }


    /**
     * Matcher to check the number of DELETE queries in {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, deleteCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> deleteCount(final int count) {
        return new QueryTypeCountMatcher(QueryType.DELETE, count);
    }

    /**
     * Matcher to check the number of OTHER queries in {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, otherCount(3)); </pre>
     */
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
