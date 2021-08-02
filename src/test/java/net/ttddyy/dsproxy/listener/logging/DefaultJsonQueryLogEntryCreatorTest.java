package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.ExecutionInfoBuilder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryInfoBuilder;
import net.ttddyy.dsproxy.StatementType;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class DefaultJsonQueryLogEntryCreatorTest {

    @Test
    public void getLogEntryForStatement() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        ExecutionInfo executionInfo = ExecutionInfoBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .isolationLevel(Connection.TRANSACTION_READ_COMMITTED)
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.STATEMENT)
                .success(true)
                .batch(false)
                .batchSize(0)
                .build();

        QueryInfo queryInfo = QueryInfoBuilder.create().query("select 1").build();

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        String jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true, true, true);
        assertThat(jsonEntry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"isolation\":\"READ_COMMITTED\", \"time\":100, \"success\":true, \"type\":\"Statement\", \"batch\":false, \"querySize\":1, \"batchSize\":0, \"query\":[\"select 1\"], \"params\":[{}]}");

        // writeDataSourceName=false
        jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), false, true, true);
        assertThat(jsonEntry).isEqualTo("{\"connection\":10, \"isolation\":\"READ_COMMITTED\", \"time\":100, \"success\":true, \"type\":\"Statement\", \"batch\":false, \"querySize\":1, \"batchSize\":0, \"query\":[\"select 1\"], \"params\":[{}]}");

        // writeConnectionId=false
        jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true, false, true);
        assertThat(jsonEntry).isEqualTo("{\"name\":\"foo\", \"isolation\":\"READ_COMMITTED\", \"time\":100, \"success\":true, \"type\":\"Statement\", \"batch\":false, \"querySize\":1, \"batchSize\":0, \"query\":[\"select 1\"], \"params\":[{}]}");

        // writeIsolation=false
        jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true, true, false);
        assertThat(jsonEntry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"time\":100, \"success\":true, \"type\":\"Statement\", \"batch\":false, \"querySize\":1, \"batchSize\":0, \"query\":[\"select 1\"], \"params\":[{}]}");

        // writeDataSourceName=false, writeConnectionId=false, writeIsolation=false
        jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), false, false, false);
        assertThat(jsonEntry).isEqualTo("{\"time\":100, \"success\":true, \"type\":\"Statement\", \"batch\":false, \"querySize\":1, \"batchSize\":0, \"query\":[\"select 1\"], \"params\":[{}]}");
    }

    @Test
    public void getLogEntryForBatchStatement() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        ExecutionInfo executionInfo = ExecutionInfoBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .isolationLevel(Connection.TRANSACTION_READ_COMMITTED)
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.STATEMENT)
                .success(true)
                .batch(true)
                .batchSize(2)
                .build();

        QueryInfo queryInfo1 = QueryInfoBuilder.create().query("select 1").build();
        QueryInfo queryInfo2 = QueryInfoBuilder.create().query("select 2").build();

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        String jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo1, queryInfo2), true, true, true);
        assertThat(jsonEntry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"isolation\":\"READ_COMMITTED\", \"time\":100, \"success\":true, \"type\":\"Statement\", \"batch\":true, \"querySize\":2, \"batchSize\":2, \"query\":[\"select 1\",\"select 2\"], \"params\":[{},{}]}");
    }

    @Test
    public void getLogEntryForPreparedStatement() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        ExecutionInfo executionInfo = ExecutionInfoBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .isolationLevel(Connection.TRANSACTION_READ_COMMITTED)
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.PREPARED)
                .success(true)
                .batch(false)
                .batchSize(0)
                .build();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .param(1, "foo")
                .param(2, 100)
                .param(3, null)  // mimic "setString(3, null)"
                .build();

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        String jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true, true, true);
        assertThat(jsonEntry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"isolation\":\"READ_COMMITTED\", \"time\":100, \"success\":true, \"type\":\"Prepared\", \"batch\":false, \"querySize\":1, \"batchSize\":0, \"query\":[\"select 1\"], \"params\":[[\"foo\",\"100\",null]]}");
    }

    @Test
    public void getLogEntryForBatchPreparedStatement() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        ExecutionInfo executionInfo = ExecutionInfoBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .isolationLevel(Connection.TRANSACTION_READ_COMMITTED)
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.PREPARED)
                .success(true)
                .batch(true)
                .batchSize(2)
                .build();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .batchParam(1, 1, "foo")
                .batchParam(1, 2, 100)
                .batchParam(2, 1, "bar")
                .batchParam(2, 2, 200)
                .build();

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        String jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true, true, true);
        assertThat(jsonEntry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"isolation\":\"READ_COMMITTED\", \"time\":100, \"success\":true, \"type\":\"Prepared\", \"batch\":true, \"querySize\":1, \"batchSize\":2, \"query\":[\"select 1\"], \"params\":[[\"foo\",\"100\"],[\"bar\",\"200\"]]}");
    }

    @Test
    public void getLogEntryForCallableStatement() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        ExecutionInfo executionInfo = ExecutionInfoBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .isolationLevel(Connection.TRANSACTION_READ_COMMITTED)
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.CALLABLE)
                .success(true)
                .batch(false)
                .batchSize(0)
                .build();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .param("name", "foo")
                .param("id", 100)
                .build();

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        String jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true, true, true);
        assertThat(jsonEntry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"isolation\":\"READ_COMMITTED\", \"time\":100, \"success\":true, \"type\":\"Callable\", \"batch\":false, \"querySize\":1, \"batchSize\":0, \"query\":[\"select 1\"], \"params\":[{\"id\":\"100\",\"name\":\"foo\"}]}");
    }

    @Test
    public void getLogEntryForBatchCallableStatement() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        ExecutionInfo executionInfo = ExecutionInfoBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .isolationLevel(Connection.TRANSACTION_READ_COMMITTED)
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.CALLABLE)
                .success(true)
                .batch(true)
                .batchSize(2)
                .build();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .batchParam(1, "name", "foo")
                .batchParam(1, "id", 100)
                .batchParam(2, "name", "bar")
                .batchParam(2, "id", 200)
                .build();

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        String jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true, true, true);
        assertThat(jsonEntry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"isolation\":\"READ_COMMITTED\", \"time\":100, \"success\":true, \"type\":\"Callable\", \"batch\":true, \"querySize\":1, \"batchSize\":2, \"query\":[\"select 1\"], \"params\":[{\"id\":\"100\",\"name\":\"foo\"},{\"id\":\"200\",\"name\":\"bar\"}]}");
    }

    @Test
    public void getLogEntryParameterOrderWithIndex() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        ExecutionInfo executionInfo = ExecutionInfoBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .isolationLevel(Connection.TRANSACTION_READ_COMMITTED)
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.PREPARED)
                .success(true)
                .batch(true)
                .batchSize(2)
                .build();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .batchParam(1, 2, 100)  // batch 1, index 2
                .batchParam(1, 3, "FOO")  // batch 1, index 3
                .batchParam(1, 1, "foo")  // batch 1, index 1
                .batchParam(2, 1, "bar")  // batch 2, index 1
                .batchParam(2, 3, "BAR")  // batch 2, index 3
                .batchParam(2, 2, 200)  // batch 2, index 2
                .build();

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        String jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true, true, true);
        assertThat(jsonEntry).containsOnlyOnce("\"params\":[[\"foo\",\"100\",\"FOO\"],[\"bar\",\"200\",\"BAR\"]]");
    }

    @Test
    public void getLogEntryParameterOrderWithNamedParam() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        ExecutionInfo executionInfo = ExecutionInfoBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .isolationLevel(Connection.TRANSACTION_READ_COMMITTED)
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.CALLABLE)
                .success(true)
                .batch(true)
                .batchSize(2)
                .build();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .batchParam(1, "c-idx", 100)
                .batchParam(1, "b-idx", "FOO")
                .batchParam(1, "a-idx", "foo")
                .batchParam(2, "b-idx", "BAR")
                .batchParam(2, "a-idx", "bar")
                .batchParam(2, "c-idx", 200)
                .build();

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        String jsonEntry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true, true, true);
        assertThat(jsonEntry).containsOnlyOnce("\"params\":[{\"a-idx\":\"foo\",\"b-idx\":\"FOO\",\"c-idx\":\"100\"},{\"a-idx\":\"bar\",\"b-idx\":\"BAR\",\"c-idx\":\"200\"}]");
    }

    @Test
    public void statementType() throws Exception {
        ExecutionInfo executionInfo;
        String jsonResult;

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        // Statement
        executionInfo = ExecutionInfoBuilder.create().statementType(StatementType.STATEMENT).build();
        jsonResult = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"type\":\"Statement\"");

        // PreparedStatement
        executionInfo = ExecutionInfoBuilder.create().statementType(StatementType.PREPARED).build();
        jsonResult = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"type\":\"Prepared\"");

        // CallableStatement
        executionInfo = ExecutionInfoBuilder.create().statementType(StatementType.CALLABLE).build();
        jsonResult = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"type\":\"Callable\"");
    }

    @Test
    public void query() throws Exception {
        ExecutionInfo executionInfo = ExecutionInfoBuilder.create().build();
        QueryInfo select1 = QueryInfoBuilder.create().query("select 1").build();
        QueryInfo select2 = QueryInfoBuilder.create().query("select 2").build();
        QueryInfo select3 = QueryInfoBuilder.create().query("select 3").build();

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();
        String jsonResult;

        // single query
        jsonResult = creator.getLogEntry(executionInfo, Arrays.asList(select1), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"query\":[\"select 1\"]");

        // multiple query
        jsonResult = creator.getLogEntry(executionInfo, Arrays.asList(select1, select2, select3), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"query\":[\"select 1\",\"select 2\",\"select 3\"]");
    }

    @Test
    public void querySize() throws Exception {
        ExecutionInfo executionInfo = ExecutionInfoBuilder.create().build();
        QueryInfo select1 = QueryInfoBuilder.create().query("select 1").build();
        QueryInfo select2 = QueryInfoBuilder.create().query("select 2").build();
        QueryInfo select3 = QueryInfoBuilder.create().query("select 3").build();

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();
        String jsonResult;

        // single query
        jsonResult = creator.getLogEntry(executionInfo, Arrays.asList(select1), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"querySize\":1");

        // multiple query
        jsonResult = creator.getLogEntry(executionInfo, Arrays.asList(select1, select2, select3), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"querySize\":3");
    }

    @Test
    public void success() throws Exception {
        ExecutionInfo executionInfo;
        String jsonResult;

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        // success
        executionInfo = ExecutionInfoBuilder.create().success(true).build();
        jsonResult = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"success\":true");

        // fail
        executionInfo = ExecutionInfoBuilder.create().success(false).build();
        jsonResult = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"success\":false");

    }

    @Test
    public void batch() throws Exception {
        ExecutionInfo executionInfo;
        String jsonResult;

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        // success
        executionInfo = ExecutionInfoBuilder.create().batch(true).build();
        jsonResult = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"batch\":true");

        // fail
        executionInfo = ExecutionInfoBuilder.create().batch(false).build();
        jsonResult = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"batch\":false");
    }

    @Test
    public void batchSize() throws Exception {
        ExecutionInfo executionInfo;
        String jsonResult;

        DefaultJsonQueryLogEntryCreator creator = new DefaultJsonQueryLogEntryCreator();

        // default
        executionInfo = ExecutionInfoBuilder.create().build();
        jsonResult = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"batchSize\":0");

        executionInfo = ExecutionInfoBuilder.create().batchSize(100).build();
        jsonResult = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true, true, true);
        assertThat(jsonResult).containsOnlyOnce("\"batchSize\":100");
    }

}
