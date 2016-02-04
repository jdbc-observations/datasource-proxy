package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;

import java.sql.JDBCType;
import java.sql.Types;

import static net.ttddyy.dsproxy.test.hamcrest.BatchParameterHolderAssertions.batch;
import static net.ttddyy.dsproxy.test.hamcrest.BatchParameterHolderAssertions.batchSize;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.isBatch;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.isBatchCallable;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.isBatchPrepared;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.isBatchStatement;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.isCallable;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.isCallableOrBatchCallable;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.isPrepared;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.isPreparedOrBatchPrepared;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.isStatement;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.isStatementOrBatchStatement;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.nullParam;
import static net.ttddyy.dsproxy.test.hamcrest.OutParameterHolderAssertions.outParam;
import static net.ttddyy.dsproxy.test.hamcrest.OutParameterHolderAssertions.outParamIndexes;
import static net.ttddyy.dsproxy.test.hamcrest.OutParameterHolderAssertions.outParamNames;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.param;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramAsInteger;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramIndexes;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramNames;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramsByIndex;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramsByName;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.batchCallableCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.batchPreparedCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.batchStatementCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.callableCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.callableOrBatchCallableCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.deleteCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.executionCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.executions;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.insertCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.preparedCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.preparedOrBatchPreparedCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.selectCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.statementCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.statementOrBatchStatementCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.totalQueryCount;
import static net.ttddyy.dsproxy.test.hamcrest.ProxyTestDataSourceAssertions.updateCount;
import static net.ttddyy.dsproxy.test.hamcrest.QueriesHolderAssertions.queries;
import static net.ttddyy.dsproxy.test.hamcrest.QueriesHolderAssertions.queryTypes;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batch;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batchCallable;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batchPrepared;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.batchStatement;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.callable;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.callableOrBatchCallable;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.failure;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.prepared;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.preparedOrBatchPrepared;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.statement;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.statementOrBatchStatement;
import static net.ttddyy.dsproxy.test.hamcrest.QueryExecutionAssertions.success;
import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.query;
import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.queryType;
import static net.ttddyy.dsproxy.test.hamcrest.QueryHolderAssertions.select;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

/**
 * API compilation check with hamcrest.
 *
 * @author Tadaya Tsuyukubo
 */
public class HamcrestAssertionApiCheck {

    private void testDataSource() {
        ProxyTestDataSource ds = new ProxyTestDataSource();

        // execution count
        assertThat(ds, executionCount(3));
        assertThat(ds, statementCount(3));
        assertThat(ds, batchStatementCount(3));
        assertThat(ds, statementOrBatchStatementCount(3));
        assertThat(ds, preparedCount(3));
        assertThat(ds, batchPreparedCount(3));
        assertThat(ds, preparedOrBatchPreparedCount(3));
        assertThat(ds, callableCount(3));
        assertThat(ds, batchCallableCount(3));
        assertThat(ds, callableOrBatchCallableCount(3));

        // each execution
        assertThat(ds, executions(0, isBatch()));
        assertThat(ds, executions(0, batch()));
        assertThat(ds, executions(0, is(batch())));

        assertThat(ds, executions(0, isStatement()));
        assertThat(ds, executions(0, statement()));
        assertThat(ds, executions(0, is(isStatement())));

        assertThat(ds, executions(0, isBatchStatement()));
        assertThat(ds, executions(0, batchStatement()));
        assertThat(ds, executions(0, is(batchStatement())));

        assertThat(ds, executions(0, isStatementOrBatchStatement()));
        assertThat(ds, executions(0, isPrepared()));
        assertThat(ds, executions(0, isBatchPrepared()));
        assertThat(ds, executions(0, isPreparedOrBatchPrepared()));
        assertThat(ds, executions(0, isCallable()));
        assertThat(ds, executions(0, isBatchCallable()));
        assertThat(ds, executions(0, isCallableOrBatchCallable()));

        assertThat(ds, executions(0, is(success())));


        // query count
        assertThat(ds, totalQueryCount(5));
        assertThat(ds, selectCount(3));
        assertThat(ds, insertCount(3));
        assertThat(ds, allOf(updateCount(3), deleteCount(1)));

        // TODO:
        // ds.reset();

        assertThat(ds.getFirstStatement(), query(is("abc")));
        assertThat(ds.getFirstBatchStatement(), queries(0, is("abc")));
        assertThat(ds.getFirstBatchCallable(), query(is("abc")));

//        List<QueryExecution> list = ds.getExecutions()
        // List<StatementExecution> list = ds.getStatements()
        // List<StatementBatchExecution> list = ds.getStatements()
        // List<PreparedExecution> list = ds.getPreparedStatements()
        // List<PreparedBatchExecution> list = ds.getPreparedStatements()
    }

    private void queryExecution() {
        QueryExecution qe = new StatementExecution();

        assertThat(qe, success());
        assertThat(qe, failure());

        // type of query
        // TODO: for QueryHolder, QueriesHolder
//        assertThat(qe, select());
//        assertThat(qe, delete());
//        assertThat(qe, anyOf(insert(), update()));

        // type of execution
        assertThat(qe, isBatch());
        assertThat(qe, statement());
        assertThat(qe, batchStatement());
        assertThat(qe, statementOrBatchStatement());
        assertThat(qe, prepared());
        assertThat(qe, batchPrepared());
        assertThat(qe, preparedOrBatchPrepared());
        assertThat(qe, callable());
        assertThat(qe, batchCallable());
        assertThat(qe, callableOrBatchCallable());

    }

    private void statementExecution() {
        StatementExecution se = new StatementExecution();

        assertThat(se, success());
        assertThat(se, failure());

        // query with StringMatcher
        assertThat(se, query(is("...")));
        assertThat(se, query(startsWith("...")));

        assertThat(se, queryType(QueryType.SELECT));
    }

    private void batchStatementExecution() {
        StatementBatchExecution sbe = new StatementBatchExecution();

        assertThat(sbe, success());
        assertThat(sbe, failure());

        assertThat(sbe, queries(0, is("...")));   // string matcher
        assertThat(sbe, queries(hasItems("...", "...")));  // collection matcher
        assertThat(sbe, queryTypes(0, is(select())));
    }

    private void preparedStatementExecution() {
        PreparedExecution pe = new PreparedExecution();

        assertThat(pe, success());
        assertThat(pe, failure());

        assertThat(pe, query(is("FOO")));  // string matcher

        // parameter indexes
        assertThat(pe, paramIndexes(1, 2, 3));
        assertThat(pe, paramIndexes(hasItem(1)));  // integer collection matcher

        // parameters
        assertThat(pe, param(1, Integer.class, is(100)));
        assertThat(pe, param(1, is((Object) 100)));  // Object matcher
        assertThat(pe, paramAsInteger(1, is(100)));
        assertThat(pe, paramsByIndex(hasEntry(1, (Object) "FOO")));  // map matcher

        // setNull parameters
        assertThat(pe, nullParam(10, Types.INTEGER));
        assertThat(pe, nullParam(10));

        assertThat(pe, allOf(paramAsInteger(1, is(100)), paramAsInteger(2, is(200)), nullParam(3)));
    }

    private void preparedBatchStatementExecution() {
        PreparedBatchExecution pbe = new PreparedBatchExecution();

        assertThat(pbe, success());
        assertThat(pbe, failure());

        assertThat(pbe, query(is("FOO")));

        // check batch executions
        assertThat(pbe, batchSize(10));

        assertThat(pbe, batch(0, paramIndexes(1, 2, 3)));
        assertThat(pbe, batch(0, paramIndexes(hasItem(11))));    // integer collection matcher

        assertThat(pbe, batch(0, param(1, Integer.class, is(100))));
        assertThat(pbe, batch(0, param(1, is((Object) 100))));  // Object matcher
        assertThat(pbe, batch(0, paramAsInteger(1, is(100))));
        assertThat(pbe, batch(0, paramsByIndex(hasEntry(11, (Object) "FOO"))));  // map matcher

        // setNull parameters
        assertThat(pbe, batch(0, nullParam(10, Types.INTEGER)));
        assertThat(pbe, batch(0, nullParam(10)));

        assertThat(pbe, batch(0, allOf(paramAsInteger(1, is(100)), paramAsInteger(2, is(200)), nullParam(3))));
    }

    private void callableStatementExecution() {
        CallableExecution ce = new CallableExecution();

        assertThat(ce, success());
        assertThat(ce, failure());

        assertThat(ce, query(is("FOO")));


        // parameter names/indexes
        assertThat(ce, paramNames("foo", "bar", "baz"));
        assertThat(ce, paramNames(hasItem("foo")));  // string collection matcher
        assertThat(ce, paramIndexes(1, 2, 3));
        assertThat(ce, paramIndexes(hasItem(11)));    // integer collection matcher

        // parameters with map matcher
        assertThat(ce, paramsByName(hasEntry("foo", (Object) "FOO")));
        assertThat(ce, paramsByIndex(hasEntry(1, (Object) "FOO")));

        // parameters
        assertThat(ce, param("foo", is((Object) 100)));
        assertThat(ce, param("foo", Integer.class, is(100)));
        assertThat(ce, paramAsInteger("foo", is(100)));
        assertThat(ce, param(1, is((Object) 100)));
        assertThat(ce, param(1, Integer.class, is(100)));
        assertThat(ce, paramAsInteger(1, is(100)));

        // setNull parameters
        assertThat(ce, nullParam("bar"));
        assertThat(ce, nullParam("bar", Types.INTEGER));
        assertThat(ce, nullParam(2));
        assertThat(ce, nullParam(2, Types.INTEGER));

        assertThat(ce, allOf(paramAsInteger(1, is(100)), paramAsInteger("foo", is(100)), nullParam("bar")));

        // registerOut parameters
        assertThat(ce, outParamNames(hasItem("foo")));
        assertThat(ce, outParamIndexes(hasItem(10)));
        assertThat(ce, outParam("foo", Types.INTEGER));
        assertThat(ce, outParam("foo", JDBCType.INTEGER));
        assertThat(ce, outParam(10, Types.INTEGER));
        assertThat(ce, outParam(10, JDBCType.INTEGER));
        assertThat(ce, allOf(outParam("foo", JDBCType.INTEGER), outParam(10, Types.INTEGER)));

        assertThat(ce, allOf(paramAsInteger(10, is(100)), paramAsInteger("foo", is(100)), outParam("bar", JDBCType.INTEGER)));
    }

    private void callableBatchStatementExecution() {
        CallableBatchExecution cbe = new CallableBatchExecution();

        assertThat(cbe, success());
        assertThat(cbe, failure());

        assertThat(cbe, query(is("FOO")));

        assertThat(cbe, batchSize(10));

        // parameter names/indexes
        assertThat(cbe, batch(0, paramNames("foo", "bar", "baz")));
        assertThat(cbe, batch(0, paramNames(hasItem("foo"))));
        assertThat(cbe, batch(0, paramIndexes(1, 2, 3)));
        assertThat(cbe, batch(0, paramIndexes(hasItem(1))));

        // parameters with map matcher
        assertThat(cbe, batch(0, paramsByName(hasEntry("foo", (Object) "FOO"))));
        assertThat(cbe, batch(0, paramsByIndex(hasEntry(1, (Object) "FOO"))));


        // parameters
        assertThat(cbe, batch(0, param("foo", is((Object) 100))));
        assertThat(cbe, batch(0, param("foo", Integer.class, is(100))));
        assertThat(cbe, batch(0, paramAsInteger("foo", is(100))));
        assertThat(cbe, batch(0, param(1, is((Object) 100))));
        assertThat(cbe, batch(0, param(1, Integer.class, is(100))));
        assertThat(cbe, batch(0, paramAsInteger(1, is(100))));

        // setNull parameters
        assertThat(cbe, batch(0, nullParam("bar")));
        assertThat(cbe, batch(0, nullParam("bar", Types.INTEGER)));
        assertThat(cbe, batch(0, nullParam(2)));
        assertThat(cbe, batch(0, nullParam(2, Types.INTEGER)));

        assertThat(cbe, batch(0, allOf(paramAsInteger(1, is(100)), paramAsInteger("foo", is(100)), nullParam("bar"))));


        // registerOut parameters
        assertThat(cbe, batch(0, outParamNames(hasItem("foo"))));
        assertThat(cbe, batch(0, outParamIndexes(hasItem(10))));
        assertThat(cbe, batch(0, outParam("foo", Types.INTEGER)));
        assertThat(cbe, batch(0, outParam("foo", JDBCType.INTEGER)));
        assertThat(cbe, batch(0, outParam(10, Types.INTEGER)));
        assertThat(cbe, batch(0, outParam(10, JDBCType.INTEGER)));
        assertThat(cbe, batch(0, allOf(outParam("foo", JDBCType.INTEGER), outParam(10, Types.INTEGER))));

        assertThat(cbe, batch(0, allOf(paramAsInteger("foo", is(100)), outParam("bar", Types.INTEGER), nullParam("baz"))));
    }

}
