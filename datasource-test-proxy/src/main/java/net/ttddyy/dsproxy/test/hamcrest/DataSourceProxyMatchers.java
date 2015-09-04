package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.test.BatchParameterHolder;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.ParameterHolder;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueriesHolder;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.QueryHolder;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.hamcrest.Matcher;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

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

    public static Matcher<? super QueriesHolder> queries(int index, Matcher<String> stringMatcher) {
        return QueriesHolderAssertions.queries(index, stringMatcher);
    }

    public static Matcher<? super QueriesHolder> queries(Matcher<? super Collection<String>> collectionMatcher) {
        return QueriesHolderAssertions.queries(collectionMatcher);
    }

    public static Matcher<? super QueriesHolder> queryTypes(int index, Matcher<? super QueryHolder> queryHolderMatcher) {
        return QueriesHolderAssertions.queryTypes(index, queryHolderMatcher);
    }

    /////////////////////////////////////////////////////////////////////////////
    // BatchParameterHolderAssertions
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Matcher to check the batch size.
     *
     * Example:
     * <pre> assertThat(ds.getBatchStatements(), batchSize(3)); </pre>
     */
    public static Matcher<? super BatchParameterHolder> batchSize(int batchSize) {
        return BatchParameterHolderAssertions.batchSize(batchSize);
    }

    /**
     * Matcher to check the given index in batch matches with given parameter matcher.
     *
     * Example:
     * <pre> assertThat(ds.getBatchStatements(), batch(0, param(1, String.class, is("FOO")))); </pre>
     */
    public static Matcher<? super BatchParameterHolder> batch(int index, Matcher<? super ParameterHolder> parameterHolderMatcher) {
        return BatchParameterHolderAssertions.batch(index, parameterHolderMatcher);
    }

    /////////////////////////////////////////////////////////////////////////////
    // ParameterHolderAssertions
    /////////////////////////////////////////////////////////////////////////////

    /**
     * Matcher to examine parameters by name as a {@link Map}(key={@link String}, value={@link Object}).
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramsByName(hasEntry("foo", (Object) "FOO"))); </pre>
     *
     * @param mapMatcher a {@link Map} matcher
     */
    public static Matcher<? super ParameterHolder> paramsByName(Matcher<Map<? extends String, ?>> mapMatcher) {
        return ParameterHolderAssertions.paramsByName(mapMatcher);
    }

    /**
     * Matcher to examine parameters by index as a {@link Map}(key={@link Integer}, value={@link Object}).
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramsByName(paramsByIndex(1, (Object) "FOO"))); </pre>
     *
     * @param mapMatcher a {@link Map} matcher
     */
    public static Matcher<? super ParameterHolder> paramsByIndex(Matcher<Map<? extends Integer, ?>> mapMatcher) {
        return ParameterHolderAssertions.paramsByIndex(mapMatcher);
    }


    /**
     * Matcher to examine parameter indexes as a {@link Collection} of {@link Integer}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramIndexes(hasItem(1), hasItem(2))); </pre>
     *
     * @param collectionMatcher a {@link Collection} matcher
     */
    public static Matcher<? super ParameterHolder> paramIndexes(Matcher<? super Collection<Integer>> collectionMatcher) {
        return ParameterHolderAssertions.paramIndexes(collectionMatcher);
    }

    /**
     * Matcher to examine parameter indexes.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramIndexes(1,2,3)); </pre>
     *
     * @param indexes parameter indexes
     */
    public static Matcher<? super ParameterHolder> paramIndexes(Integer... indexes) {
        return ParameterHolderAssertions.paramIndexes(indexes);
    }

    /**
     * Matcher to examine parameter names as a {@link Collection} of {@link String}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramNames(hasItem("foo"), hasItem("bar"))); </pre>
     *
     * @param collectionMatcher a {@link Collection} matcher
     */
    public static Matcher<? super ParameterHolder> paramNames(Matcher<? super Collection<String>> collectionMatcher) {
        return ParameterHolderAssertions.paramNames(collectionMatcher);
    }

    /**
     * Matcher to examine parameter names.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramNames("foo","bar","baz")); </pre>
     *
     * @param names parameter names
     */
    public static Matcher<? super ParameterHolder> paramNames(String... names) {
        return ParameterHolderAssertions.paramNames(names);
    }


    /**
     * Matcher to examine parameter by name.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, param("foo", is((Object) "FOO"))); </pre>
     *
     * @param name parameter name
     */
    public static Matcher<? super ParameterHolder> param(String name, Matcher<Object> matcher) {
        return ParameterHolderAssertions.param(name, matcher);

    }

    /**
     * Matcher to examine parameter by index.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, param(1, is((Object) "FOO"))); </pre>
     *
     * @param index parameter index
     */
    public static Matcher<? super ParameterHolder> param(Integer index, Matcher<Object> matcher) {
        return ParameterHolderAssertions.param(index, matcher);

    }

    /**
     * Matcher to examine parameter by name with given class type value.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, param("foo", String.class, is("FOO"))); </pre>
     *
     * @param name  parameter name
     * @param clazz value type
     */
    public static <T> Matcher<? super ParameterHolder> param(String name, Class<T> clazz, Matcher<? super T> matcher) {
        return ParameterHolderAssertions.param(name, clazz, matcher);
    }

    /**
     * Matcher to examine parameter by index with given class type value.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, param(1, String.class, is("FOO"))); </pre>
     *
     * @param index parameter index
     * @param clazz value type
     */
    public static <T> Matcher<? super ParameterHolder> param(int index, Class<T> clazz, Matcher<? super T> matcher) {
        return ParameterHolderAssertions.param(index, clazz, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link String}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsString(1, is("FOO"))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsString(Integer index, Matcher<? super String> matcher) {
        return ParameterHolderAssertions.paramAsString(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Integer}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsInteger(1, is(100))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsInteger(Integer index, Matcher<? super Integer> matcher) {
        return ParameterHolderAssertions.paramAsInteger(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Long}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsLong(1, is(100L))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsLong(Integer index, Matcher<? super Long> matcher) {
        return ParameterHolderAssertions.paramAsLong(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Double}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsDouble(1, is(10.0))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsDouble(Integer index, Matcher<? super Double> matcher) {
        return ParameterHolderAssertions.paramAsDouble(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Short}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsShort(1, is((short)1))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsShort(Integer index, Matcher<? super Short> matcher) {
        return ParameterHolderAssertions.paramAsShort(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Boolean}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsBoolean(1, is(true))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBoolean(Integer index, Matcher<? super Boolean> matcher) {
        return ParameterHolderAssertions.paramAsBoolean(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Byte}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsByte(1, is((byte)1))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsByte(Integer index, Matcher<? super Byte> matcher) {
        return ParameterHolderAssertions.paramAsByte(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Float}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsFloat(1, is((float)1.0))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsFloat(Integer index, Matcher<? super Float> matcher) {
        return ParameterHolderAssertions.paramAsFloat(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link BigDecimal}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsBigDecimal(1, is(new BigDecimal(10))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBigDecimal(Integer index, Matcher<? super BigDecimal> matcher) {
        return ParameterHolderAssertions.paramAsBigDecimal(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@code byte[]}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsBytes(1, is(new byte[]{0xa, 0xb}))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBytes(Integer index, Matcher<? super byte[]> matcher) {
        return ParameterHolderAssertions.paramAsBytes(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Date}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsDate(1, is((float)1.0))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsDate(Integer index, Matcher<? super Date> matcher) {
        return ParameterHolderAssertions.paramAsDate(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Time}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsTime(1, is(new Time(1000)))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsTime(Integer index, Matcher<? super Time> matcher) {
        return ParameterHolderAssertions.paramAsTime(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Timestamp}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsTimestamp(1, is(new Timestamp(1000)))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsTimestamp(Integer index, Matcher<? super Timestamp> matcher) {
        return ParameterHolderAssertions.paramAsTimestamp(index, matcher);
    }

    /**
     * Matcher to examine parameter by index with value as {@link Array}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramAsArray(1, is(array))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsArray(Integer index, Matcher<? super Array> matcher) {
        return ParameterHolderAssertions.paramAsArray(index, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link String}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsString("foo", is("FOO"))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsString(String name, Matcher<? super String> matcher) {
        return ParameterHolderAssertions.paramAsString(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Integer}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsInteger("foo", is(100))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsInteger(String name, Matcher<? super Integer> matcher) {
        return ParameterHolderAssertions.paramAsInteger(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Long}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsLong("foo", is(100L))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsLong(String name, Matcher<? super Long> matcher) {
        return ParameterHolderAssertions.paramAsLong(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Double}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsDouble("foo", is(10.0))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsDouble(String name, Matcher<? super Double> matcher) {
        return ParameterHolderAssertions.paramAsDouble(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Short}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsShort("foo", is((short)1))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsShort(String name, Matcher<? super Short> matcher) {
        return ParameterHolderAssertions.paramAsShort(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Boolean}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsBoolean("foo", is(true))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBoolean(String name, Matcher<? super Boolean> matcher) {
        return ParameterHolderAssertions.paramAsBoolean(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@code byte[]}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsBytes("foo", is(new byte[]{0xa, 0xb}))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsByte(String name, Matcher<? super Byte> matcher) {
        return ParameterHolderAssertions.paramAsByte(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Float}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsFloat("foo", is((float)1.0))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsFloat(String name, Matcher<? super Float> matcher) {
        return ParameterHolderAssertions.paramAsFloat(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link BigDecimal}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsBigDecimal("foo", is(new BigDecimal(10))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBigDecimal(String name, Matcher<? super BigDecimal> matcher) {
        return ParameterHolderAssertions.paramAsBigDecimal(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@code byte[]}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsBytes("foo", is(new byte[]{0xa, 0xb}))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsBytes(String name, Matcher<? super byte[]> matcher) {
        return ParameterHolderAssertions.paramAsBytes(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Time}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsTime("foo", is(new Time(1000)))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsDate(String name, Matcher<? super Date> matcher) {
        return ParameterHolderAssertions.paramAsDate(name, matcher);
    }


    /**
     * Matcher to examine parameter by name with value as {@link Time}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsTime("foo", is(new Time(1000)))); </pre>
     */

    public static Matcher<? super ParameterHolder> paramAsTime(String name, Matcher<? super Time> matcher) {
        return ParameterHolderAssertions.paramAsTime(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Timestamp}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsTimestamp("foo", is(new Timestamp(1000)))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsTimestamp(String name, Matcher<? super Timestamp> matcher) {
        return ParameterHolderAssertions.paramAsTimestamp(name, matcher);
    }

    /**
     * Matcher to examine parameter by name with value as {@link Array}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramAsArray("foo", is(array))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramAsArray(String name, Matcher<? super Array> matcher) {
        return ParameterHolderAssertions.paramAsArray(name, matcher);
    }

    /**
     * Matcher to examine parameter by index is {@code setNull} operation with given {@link java.sql.Types}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramSetNull(1, is(Types.VARCHAR))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramSetNull(int index, int sqlType) {
        return ParameterHolderAssertions.paramSetNull(index, sqlType);
    }

    /**
     * Matcher to examine parameter by name is {@code setNull} operation with given {@link java.sql.Types}.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramSetNull("foo", is(Types.VARCHAR))); </pre>
     */
    public static Matcher<? super ParameterHolder> paramSetNull(String name, int sqlType) {
        return ParameterHolderAssertions.paramSetNull(name, sqlType);
    }

    /**
     * Matcher to examine parameter by name is {@code setNull} operation.
     * <p>
     * Example:
     * <pre> assertThat(parameterByNameHolder, paramSetNull("foo")); </pre>
     */
    public static Matcher<? super ParameterHolder> paramSetNull(String name) {
        return ParameterHolderAssertions.paramSetNull(name);
    }

    /**
     * Matcher to examine parameter by index is {@code setNull} operation.
     * <p>
     * Example:
     * <pre> assertThat(parameterByIndexHolder, paramSetNull(1)); </pre>
     */
    public static Matcher<? super ParameterHolder> paramSetNull(int index) {
        return ParameterHolderAssertions.paramSetNull(index);
    }


    /////////////////////////////////////////////////////////////////////////////
    // OutParameterHolderAssertions
    /////////////////////////////////////////////////////////////////////////////


}
