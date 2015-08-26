package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueriesHolder;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.QueryHolder;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

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


    public static Matcher<ProxyTestDataSource> executionCount(int count) {
        String msg = "queryExecutions size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getQueryExecutions().size();
            }
        };
    }

    public static Matcher<ProxyTestDataSource> statementCount(int count) {
        String msg = "StatementExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getStatements().size();
            }
        };
    }

    public static Matcher<ProxyTestDataSource> batchStatementCount(int count) {
        String msg = "StatementBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getBatchStatements().size();
            }
        };
    }

    public static Matcher<ProxyTestDataSource> statementOrBatchStatementCount(int count) {
        String msg = "StatementExecution or StatementBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getStatements().size() + actual.getBatchStatements().size();
            }
        };
    }

    public static Matcher<ProxyTestDataSource> preparedCount(int count) {
        String msg = "PreparedExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getPrepareds().size();
            }
        };
    }

    public static Matcher<ProxyTestDataSource> batchPreparedCount(int count) {
        String msg = "PreparedBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getBatchPrepareds().size();
            }
        };
    }

    public static Matcher<ProxyTestDataSource> preparedOrBatchPreparedCount(int count) {
        String msg = "PreparedExecution or PreparedBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getPrepareds().size() + actual.getBatchPrepareds().size();
            }
        };
    }

    public static Matcher<ProxyTestDataSource> callableCount(int count) {
        String msg = "CallableExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getCallables().size();
            }
        };

    }

    public static Matcher<ProxyTestDataSource> batchCallableCount(int count) {
        String msg = "CallableBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getBatchCallables().size();
            }
        };

    }

    public static Matcher<ProxyTestDataSource> callableOrBatchCallableCount(int count) {
        String msg = "CallableExecution or CallableBatchExecution size";
        return new FeatureMatcher<ProxyTestDataSource, Integer>(equalTo(count), msg, msg) {
            @Override
            protected Integer featureValueOf(ProxyTestDataSource actual) {
                return actual.getCallables().size() + actual.getBatchCallables().size();
            }
        };
    }


    // TODO: document difference of executionCount()  (BatchPrepared)

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
