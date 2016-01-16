package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.listener.QueryUtils;
import net.ttddyy.dsproxy.test.DefaultQueryExtractor;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.QueryExtractor;
import net.ttddyy.dsproxy.test.hamcrest.ExecutionType;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.data.Index;
import org.assertj.core.internal.Objects;
import org.assertj.core.util.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

/**
 * AssertJ assertions for {@link ProxyTestDataSource}.
 *
 * @author Tadaya Tsuyukubo
 */
public class ProxyTestDataSourceAssert extends AbstractAssert<ProxyTestDataSourceAssert, ProxyTestDataSource> {

    private static final int QUERY_LENGTH_TO_SHOW = 12;

    private QueryExtractor queryExtractor = new DefaultQueryExtractor();

    @VisibleForTesting
    protected Objects objects = Objects.instance();


    public ProxyTestDataSourceAssert(ProxyTestDataSource actual) {
        super(actual, ProxyTestDataSourceAssert.class);
    }

    public ProxyTestDataSourceAssert hasExecutionType(ExecutionType executionType, final Index index) {
        isNotNull();
        List<QueryExecution> queryExecutions = actual.getQueryExecutions();
        if (queryExecutions.size() <= index.value) {
            failWithMessage("Expecting: index <%s> is less than the size of query executions <%s>", index.value, queryExecutions.size());
        }

        QueryExecution queryExecution = queryExecutions.get(index.value);
        Class[] expectedExecutionTypes = executionType.getExecutionTypes();
        objects.assertIsInstanceOfAny(info, queryExecution, expectedExecutionTypes);
        return this;
    }

    public ProxyTestDataSourceAssert hasExecutionCount(int count) {
        checkExecutionCount(count, "executions", actual.getQueryExecutions());
        return this;
    }

    public ProxyTestDataSourceAssert hasStatementCount(int count) {
        checkExecutionCount(count, "statement executions", actual.getStatements());
        return this;
    }

    public ProxyTestDataSourceAssert hasBatchStatementCount(int count) {
        checkExecutionCount(count, "batch statement executions", actual.getBatchStatements());
        return this;
    }

    public ProxyTestDataSourceAssert hasStatementOrBatchStatementCount(int count) {
        List<QueryExecution> executions = new ArrayList<QueryExecution>();
        executions.addAll(actual.getStatements());
        executions.addAll(actual.getBatchStatements());
        checkExecutionCount(count, "statement or batch statement executions", executions);
        return this;
    }

    public ProxyTestDataSourceAssert hasPreparedCount(int count) {
        checkExecutionCount(count, "prepared executions", actual.getPrepareds());
        return this;
    }

    public ProxyTestDataSourceAssert hasBatchPreparedCount(int count) {
        checkExecutionCount(count, "batch prepared executions", actual.getBatchPrepareds());
        return this;
    }

    public ProxyTestDataSourceAssert hasPreparedOrBatchPreparedCount(int count) {
        List<QueryExecution> executions = new ArrayList<QueryExecution>();
        executions.addAll(actual.getPrepareds());
        executions.addAll(actual.getBatchPrepareds());
        checkExecutionCount(count, "prepared or batch prepared executions", executions);
        return this;
    }

    public ProxyTestDataSourceAssert hasCallableCount(int count) {
        checkExecutionCount(count, "callable executions", actual.getCallables());
        return this;
    }

    public ProxyTestDataSourceAssert hasBatchCallableCount(int count) {
        checkExecutionCount(count, "batch callable executions", actual.getBatchCallables());
        return this;
    }

    public ProxyTestDataSourceAssert hasCallableOrBatchCallableCount(int count) {
        List<QueryExecution> executions = new ArrayList<QueryExecution>();
        executions.addAll(actual.getCallables());
        executions.addAll(actual.getBatchCallables());
        checkExecutionCount(count, "callable or batch callable executions", executions);
        return this;
    }

    private void checkExecutionCount(int expectedCount, String executionType, List<? extends QueryExecution> executions) {
        isNotNull();
        if (executions == null) {
            failWithMessage("executions is %s null");
        } else if (executions.size() != expectedCount) {
            failWithMessage("Expected %s size: <%d> but was <%d>", executionType, expectedCount, executions.size());
        }
    }


    public ProxyTestDataSourceAssert hasTotalQueryCount(int count) {
        checkQueryCount(null, count);
        return this;
    }

    public ProxyTestDataSourceAssert hasSelectCount(int count) {
        checkQueryCount(QueryType.SELECT, count);
        return this;
    }

    public ProxyTestDataSourceAssert hasInsertCount(int count) {
        checkQueryCount(QueryType.INSERT, count);
        return this;
    }

    public ProxyTestDataSourceAssert hasUpdateCount(int count) {
        checkQueryCount(QueryType.UPDATE, count);
        return this;
    }

    public ProxyTestDataSourceAssert hasDeleteCount(int count) {
        checkQueryCount(QueryType.DELETE, count);
        return this;
    }

    public ProxyTestDataSourceAssert hasOtherCount(int count) {
        checkQueryCount(QueryType.OTHER, count);
        return this;
    }

    private void checkQueryCount(QueryType expectedType, int expectedCount) {
        isNotNull();

        boolean isTotal = expectedType == null;

        List<String> allQueries = new ArrayList<String>();

        // get counts
        int actualCount = 0;
        for (QueryExecution queryExecution : actual.getQueryExecutions()) {
            List<String> queries = queryExtractor.getQueries(queryExecution);
            for (String query : queries) {
                QueryType queryType = QueryUtils.getQueryType(query);
                if (isTotal || expectedType.equals(queryType)) {
                    actualCount++;
                }

                query = query.trim();
                if (query.length() > QUERY_LENGTH_TO_SHOW) {
                    query = query.substring(0, QUERY_LENGTH_TO_SHOW) + "...";
                }
                allQueries.add(query);  // for error message purpose
            }
        }

        if (expectedCount != actualCount) {
            if (expectedType == null) {
                failWithMessage("Expected query count: <%d> but was <%d>: %s", expectedCount, actualCount, allQueries);
            } else {
                failWithMessage("Expected %s count: <%d> but was <%d>: %s", expectedType, expectedCount, actualCount, allQueries);
            }
        }

    }

}

