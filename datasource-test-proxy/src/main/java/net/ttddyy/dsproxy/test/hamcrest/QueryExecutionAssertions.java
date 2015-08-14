package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.test.QueryExecution;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class QueryExecutionAssertions {

    public static Matcher<QueryExecution> success() {
        return new TypeSafeMatcher<QueryExecution>() {
            @Override
            protected boolean matchesSafely(QueryExecution item) {
                return item.isSuccess();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("success");
            }

            @Override
            protected void describeMismatchSafely(QueryExecution item, Description mismatchDescription) {
                mismatchDescription.appendText("was failure");
            }
        };
    }

    public static Matcher<QueryExecution> failure() {
        return new TypeSafeMatcher<QueryExecution>() {
            @Override
            protected boolean matchesSafely(QueryExecution item) {
                return !item.isSuccess();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("failure");
            }

            @Override
            protected void describeMismatchSafely(QueryExecution item, Description mismatchDescription) {
                mismatchDescription.appendText("was success");
            }
        };
    }


    public static Matcher<? super QueryExecution> batch() {
        return new ExecutionTypeMatcher(ExecutionType.IS_BATCH);
    }

    public static Matcher<? super QueryExecution> statement() {
        return new ExecutionTypeMatcher(ExecutionType.IS_STATEMENT);
    }

    public static Matcher<? super QueryExecution> batchStatement() {
        return new ExecutionTypeMatcher(ExecutionType.IS_BATCH_STATEMENT);
    }

    public static Matcher<? super QueryExecution> statementOrBatchStatement() {
        return new ExecutionTypeMatcher(ExecutionType.IS_STATEMENT_OR_BATCH_STATEMENT);
    }

    public static Matcher<? super QueryExecution> prepared() {
        return new ExecutionTypeMatcher(ExecutionType.IS_PREPARED);
    }

    public static Matcher<? super QueryExecution> batchPrepared() {
        return new ExecutionTypeMatcher(ExecutionType.IS_BATCH_PREPARED);
    }

    public static Matcher<? super QueryExecution> preparedOrBatchPrepared() {
        return new ExecutionTypeMatcher(ExecutionType.IS_PREPARED_OR_BATCH_PREPARED);
    }

    public static Matcher<? super QueryExecution> callable() {
        return new ExecutionTypeMatcher(ExecutionType.IS_CALLABLE);
    }

    public static Matcher<? super QueryExecution> batchCallable() {
        return new ExecutionTypeMatcher(ExecutionType.IS_BATCH_CALLABLE);
    }

    public static Matcher<? super QueryExecution> callableOrBatchCallable() {
        return new ExecutionTypeMatcher(ExecutionType.IS_CALLABLE_OR_BATCH_CALLABLE);
    }

    public static Matcher<? super QueryExecution> select() {
        return new QueryTypeMatcher(QueryType.SELECT);
    }

    public static Matcher<? super QueryExecution> insert() {
        return new QueryTypeMatcher(QueryType.INSERT);
    }

    public static Matcher<? super QueryExecution> update() {
        return new QueryTypeMatcher(QueryType.UPDATE);
    }

    public static Matcher<? super QueryExecution> delete() {
        return new QueryTypeMatcher(QueryType.DELETE);
    }

    public static Matcher<? super QueryExecution> other() {
        return new QueryTypeMatcher(QueryType.OTHER);
    }


    private static class QueryTypeMatcher extends TypeSafeMatcher<QueryExecution> {

        private QueryType expectedQueryType;

        private QueryTypeMatcher(QueryType expectedQueryType) {
            this.expectedQueryType = expectedQueryType;
        }

        @Override
        protected boolean matchesSafely(QueryExecution item) {
            return this.expectedQueryType.equals(item.getQueryType());
        }

        @Override
        public void describeTo(Description description) {
            // expected clause
            description.appendText(this.expectedQueryType.name());
        }

        @Override
        protected void describeMismatchSafely(QueryExecution item, Description mismatchDescription) {
            // but was clause
            mismatchDescription.appendText("was ");
            QueryType queryType = item.getQueryType();
            if (queryType == null) {
                mismatchDescription.appendText(null);
            } else {
                mismatchDescription.appendText(queryType.name());
            }
        }

    }


    // TODO: impl
    // alias of batch()
    public static Matcher<? super QueryExecution> isBatch() {
        return null;
    }

    // TODO: impl
    public static Matcher<? super QueryExecution> isStatement() {
        return null;
    }

    // TODO: impl
    public static Matcher<? super QueryExecution> isBatchStatement() {
        return null;
    }

    // TODO: impl
    public static Matcher<? super QueryExecution> isStatementOrBatchStatement() {
        return null;
    }

    // TODO: impl
    public static Matcher<? super QueryExecution> isPrepared() {
        return null;
    }

    // TODO: impl
    public static Matcher<? super QueryExecution> isBatchPrepared() {
        return null;
    }

    // TODO: impl
    public static Matcher<? super QueryExecution> isPreparedOrBatchPrepared() {
        return null;
    }

    // TODO: impl
    public static Matcher<? super QueryExecution> isCallable() {
        return null;
    }

    // TODO: impl
    public static Matcher<? super QueryExecution> isBatchCallable() {
        return null;
    }

    // TODO: impl
    public static Matcher<? super QueryExecution> isCallableOrBatchCallable() {
        return null;
    }


    // TODO: isUpdate, isSelect, isDelete, isInsert, isOther
}
