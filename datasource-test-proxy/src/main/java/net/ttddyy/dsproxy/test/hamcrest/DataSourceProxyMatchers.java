package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.test.BatchParameterHolder;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.ParameterHolder;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.QueryHolder;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.hamcrest.Matcher;

/**
 * Aggregated hamcrest matchers.
 *
 * @author Tadaya Tsuyukubo
 * @see ProxyTestDataSourceAssertions
 * @see QueryExecutionAssertions
 * @see QueryHolderAssertions
 * @see QueriesHolderAssertions
 * @see BatchParameterHolderAssertions
 * @see ParameterHolderAssertions
 * @see OutParameterHolderAssertions
 * @since 1.4
 */
public class DataSourceProxyMatchers {

    /////////////////////////////////////////////////////////////////////////////
    // Matchers from ProxyTestDataSourceAssertions
    /////////////////////////////////////////////////////////////////////////////

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
        return ProxyTestDataSourceAssertions.executions(index, executionType);
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
    public static Matcher<ProxyTestDataSource> executions(int index, Matcher<? super QueryExecution> queryExecutionMatcher) {
        return ProxyTestDataSourceAssertions.executions(index, queryExecutionMatcher);
    }

    /**
     * Matcher to check the number of {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, executionCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> executionCount(int count) {
        return ProxyTestDataSourceAssertions.executionCount(count);
    }

    /**
     * Matcher to check the number of {@link StatementExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, statementCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> statementCount(int count) {
        return ProxyTestDataSourceAssertions.statementCount(count);
    }

    /**
     * Matcher to check the number of {@link StatementBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, batchStatementCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> batchStatementCount(int count) {
        return ProxyTestDataSourceAssertions.batchStatementCount(count);
    }

    /**
     * Matcher to check the number of {@link StatementExecution} or {@link StatementBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, statementOrBatchStatementCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> statementOrBatchStatementCount(int count) {
        return ProxyTestDataSourceAssertions.statementOrBatchStatementCount(count);
    }


    /**
     * Matcher to check the number of {@link PreparedExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, preparedCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> preparedCount(int count) {
        return ProxyTestDataSourceAssertions.preparedCount(count);
    }


    /**
     * Matcher to check the number of {@link PreparedBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, batchPreparedCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> batchPreparedCount(int count) {
        return ProxyTestDataSourceAssertions.batchPreparedCount(count);
    }


    /**
     * Matcher to check the number of {@link PreparedExecution} or {@link PreparedBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, preparedOrBatchPreparedCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> preparedOrBatchPreparedCount(int count) {
        return ProxyTestDataSourceAssertions.preparedOrBatchPreparedCount(count);
    }


    /**
     * Matcher to check the number of {@link CallableExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, callableCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> callableCount(int count) {
        return ProxyTestDataSourceAssertions.callableCount(count);
    }

    /**
     * Matcher to check the number of {@link CallableBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, batchCallableCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> batchCallableCount(int count) {
        return ProxyTestDataSourceAssertions.batchCallableCount(count);
    }

    /**
     * Matcher to check the number of {@link CallableExecution} or {@link CallableBatchExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, callableOrBatchCallableCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> callableOrBatchCallableCount(int count) {
        return ProxyTestDataSourceAssertions.callableOrBatchCallableCount(count);
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
    public static Matcher<ProxyTestDataSource> totalQueryCount(int count) {
        return ProxyTestDataSourceAssertions.totalQueryCount(count);
    }


    /**
     * Matcher to check the number of SELECT queries in {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, selectCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> selectCount(int count) {
        return ProxyTestDataSourceAssertions.selectCount(count);
    }

    /**
     * Matcher to check the number of INSERT queries in {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, insertCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> insertCount(int count) {
        return ProxyTestDataSourceAssertions.insertCount(count);
    }

    /**
     * Matcher to check the number of UPDATE queries in {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, updateCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> updateCount(int count) {
        return ProxyTestDataSourceAssertions.updateCount(count);
    }

    /**
     * Matcher to check the number of DELETE queries in {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, deleteCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> deleteCount(int count) {
        return ProxyTestDataSourceAssertions.deleteCount(count);
    }

    /**
     * Matcher to check the number of OTHER queries in {@link QueryExecution} in {@link ProxyTestDataSource}.
     *
     * Example:
     * <pre> assertThat(ds, otherCount(3)); </pre>
     */
    public static Matcher<ProxyTestDataSource> otherCount(int count) {
        return ProxyTestDataSourceAssertions.otherCount(count);
    }


    /////////////////////////////////////////////////////////////////////////////
    // Matchers from QueryExecutionAssertions
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Matcher to check {@link QueryExecution} was successful.
     *
     * Example:
     * <pre> assertThat(qe, success()); </pre>
     */
    public static Matcher<QueryExecution> success() {
        return QueryExecutionAssertions.success();
    }

    /**
     * Matcher to check {@link QueryExecution} was failure.
     *
     * Example:
     * <pre> assertThat(qe, failure()); </pre>
     */
    public static Matcher<QueryExecution> failure() {
        return QueryExecutionAssertions.failure();
    }


    /**
     * Matcher to check {@link QueryExecution} was a batch execution.
     *
     * Example:
     * <pre> assertThat(qe, batch()); </pre>
     */
    public static Matcher<? super QueryExecution> batch() {
        return QueryExecutionAssertions.batch();
    }

    /**
     * Matcher to check {@link QueryExecution} was an execution of {@link java.sql.Statement}.
     *
     * Example:
     * <pre> assertThat(qe, statement()); </pre>
     */
    public static Matcher<? super QueryExecution> statement() {
        return QueryExecutionAssertions.statement();
    }

    /**
     * Matcher to check {@link QueryExecution} was a batch execution of {@link java.sql.Statement}.
     *
     * Example:
     * <pre> assertThat(qe, batchStatement()); </pre>
     */
    public static Matcher<? super QueryExecution> batchStatement() {
        return QueryExecutionAssertions.batchStatement();
    }

    /**
     * Matcher to check {@link QueryExecution} was a normal or batch execution of {@link java.sql.Statement}.
     *
     * Example:
     * <pre> assertThat(qe, statementOrBatchStatement()); </pre>
     */
    public static Matcher<? super QueryExecution> statementOrBatchStatement() {
        return QueryExecutionAssertions.statementOrBatchStatement();
    }

    /**
     * Matcher to check {@link QueryExecution} was an execution of {@link java.sql.PreparedStatement}.
     *
     * Example:
     * <pre> assertThat(qe, prepared()); </pre>
     */
    public static Matcher<? super QueryExecution> prepared() {
        return QueryExecutionAssertions.prepared();
    }

    /**
     * Matcher to check {@link QueryExecution} was a batch execution of {@link java.sql.PreparedStatement}.
     *
     * Example:
     * <pre> assertThat(qe, batchPrepared()); </pre>
     */
    public static Matcher<? super QueryExecution> batchPrepared() {
        return QueryExecutionAssertions.batchPrepared();
    }

    /**
     * Matcher to check {@link QueryExecution} was a normal or batch execution of {@link java.sql.PreparedStatement}.
     *
     * Example:
     * <pre> assertThat(qe, preparedOrBatchPrepared()); </pre>
     */
    public static Matcher<? super QueryExecution> preparedOrBatchPrepared() {
        return QueryExecutionAssertions.preparedOrBatchPrepared();
    }

    /**
     * Matcher to check {@link QueryExecution} was an execution of {@link java.sql.CallableStatement}.
     *
     * Example:
     * <pre> assertThat(qe, callable()); </pre>
     */
    public static Matcher<? super QueryExecution> callable() {
        return QueryExecutionAssertions.callable();
    }

    /**
     * Matcher to check {@link QueryExecution} was a batch execution of {@link java.sql.CallableStatement}.
     *
     * Example:
     * <pre> assertThat(qe, batchCallable()); </pre>
     */
    public static Matcher<? super QueryExecution> batchCallable() {
        return QueryExecutionAssertions.batchCallable();
    }

    /**
     * Matcher to check {@link QueryExecution} was a normal or batch execution of {@link java.sql.CallableStatement}.
     *
     * Example:
     * <pre> assertThat(qe, callableOrBatchCallable()); </pre>
     */
    public static Matcher<? super QueryExecution> callableOrBatchCallable() {
        return QueryExecutionAssertions.callableOrBatchCallable();
    }


    /**
     * Matcher to check {@link QueryExecution} was a batch execution.
     * Alias of {@link #batch()}
     *
     * Example:
     * <pre> assertThat(qe, isBatch()); </pre>
     */
    public static Matcher<? super QueryExecution> isBatch() {
        return batch();
    }

    /**
     * Matcher to check {@link QueryExecution} was an execution of {@link java.sql.Statement}.
     * Alias of {@link #statement()}
     *
     * Example:
     * <pre> assertThat(qe, isStatement()); </pre>
     */
    public static Matcher<? super QueryExecution> isStatement() {
        return statement();
    }


    /**
     * Matcher to check {@link QueryExecution} was a batch execution of {@link java.sql.Statement}.
     * Alias of {@link #batchStatement()}
     *
     * Example:
     * <pre> assertThat(qe, isBatchStatement()); </pre>
     */
    public static Matcher<? super QueryExecution> isBatchStatement() {
        return batchStatement();
    }


    /**
     * Matcher to check {@link QueryExecution} was a normal or batch execution of {@link java.sql.Statement}.
     * Alias of {@link #statementOrBatchStatement()}
     *
     * Example:
     * <pre> assertThat(qe, isStatementOrBatchStatement()); </pre>
     */
    public static Matcher<? super QueryExecution> isStatementOrBatchStatement() {
        return statementOrBatchStatement();
    }

    /**
     * Matcher to check {@link QueryExecution} was an execution of {@link java.sql.PreparedStatement}.
     * Alias of {@link #prepared()}
     *
     * Example:
     * <pre> assertThat(qe, isPrepared()); </pre>
     */
    public static Matcher<? super QueryExecution> isPrepared() {
        return prepared();
    }

    /**
     * Matcher to check {@link QueryExecution} was a batch execution of {@link java.sql.PreparedStatement}.
     * Alias of {@link #batchPrepared()}
     *
     * Example:
     * <pre> assertThat(qe, isBatchPrepared()); </pre>
     */
    public static Matcher<? super QueryExecution> isBatchPrepared() {
        return batchPrepared();
    }

    /**
     * Matcher to check {@link QueryExecution} was a normal or batch execution of {@link java.sql.PreparedStatement}.
     * Alias of {@link #preparedOrBatchPrepared()}
     *
     * Example:
     * <pre> assertThat(qe, isPreparedOrBatchPrepared()); </pre>
     */
    public static Matcher<? super QueryExecution> isPreparedOrBatchPrepared() {
        return preparedOrBatchPrepared();
    }

    /**
     * Matcher to check {@link QueryExecution} was an execution of {@link java.sql.CallableStatement}.
     * Alias of {@link #callable()}
     *
     * Example:
     * <pre> assertThat(qe, isCallable()); </pre>
     */
    public static Matcher<? super QueryExecution> isCallable() {
        return callable();
    }

    /**
     * Matcher to check {@link QueryExecution} was a batch execution of {@link java.sql.CallableStatement}.
     * Alias of {@link #batchCallable()}
     *
     * Example:
     * <pre> assertThat(qe, isBatchCallable()); </pre>
     */
    public static Matcher<? super QueryExecution> isBatchCallable() {
        return batchCallable();
    }

    /**
     * Matcher to check {@link QueryExecution} was a normal or batch execution of {@link java.sql.CallableStatement}.
     * Alias of {@link #callableOrBatchCallable()}
     *
     * Example:
     * <pre> assertThat(qe, isCallableOrBatchCallable()); </pre>
     */
    public static Matcher<? super QueryExecution> isCallableOrBatchCallable() {
        return callableOrBatchCallable();
    }

    /////////////////////////////////////////////////////////////////////////////
    // QueryHolderAssertions
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Matcher to examine the query with given {@link String} matcher.
     *
     * Example:
     * <pre> assertThat(qe, query(startsWith("select"))); </pre>
     */
    public static Matcher<? super QueryHolder> query(Matcher<String> stringMatcher) {
        return QueryHolderAssertions.query(stringMatcher);
    }

    /**
     * Matcher to examine the query type.
     *
     * Example:
     * <pre> assertThat(qe, queryType(SELECT)); </pre>
     */
    public static Matcher<? super QueryHolder> queryType(QueryType expectedType) {
        return QueryHolderAssertions.queryType(expectedType);
    }

    /**
     * Matcher to examine the query type is SELECT.
     *
     * Example:
     * <pre> assertThat(qe, select()); </pre>
     */
    public static Matcher<? super QueryHolder> select() {
        return QueryHolderAssertions.select();
    }

    /**
     * Matcher to examine the query type is SELECT.
     *
     * Example:
     * <pre> assertThat(qe, insert()); </pre>
     */
    public static Matcher<? super QueryHolder> insert() {
        return QueryHolderAssertions.insert();
    }

    /**
     * Matcher to examine the query type is SELECT.
     *
     * Example:
     * <pre> assertThat(qe, update()); </pre>
     */
    public static Matcher<? super QueryHolder> update() {
        return QueryHolderAssertions.update();
    }

    /**
     * Matcher to examine the query type is SELECT.
     *
     * Example:
     * <pre> assertThat(qe, delete()); </pre>
     */
    public static Matcher<? super QueryHolder> delete() {
        return QueryHolderAssertions.delete();
    }

    /**
     * Matcher to examine the query type is SELECT.
     *
     * Example:
     * <pre> assertThat(qe, other()); </pre>
     */
    public static Matcher<? super QueryHolder> other() {
        return QueryHolderAssertions.other();
    }

    /////////////////////////////////////////////////////////////////////////////
    // QueriesHolderAssertions
    /////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
    // BatchParameterHolderAssertions
    /////////////////////////////////////////////////////////////////////////////


    public static Matcher<? super BatchParameterHolder> batch(int index, Matcher<? super ParameterHolder> parameterHolderMatcher) {
        return BatchParameterHolderAssertions.batch(index, parameterHolderMatcher);
    }

    /////////////////////////////////////////////////////////////////////////////
    // ParameterHolderAssertions
    /////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
    // OutParameterHolderAssertions
    /////////////////////////////////////////////////////////////////////////////


}
