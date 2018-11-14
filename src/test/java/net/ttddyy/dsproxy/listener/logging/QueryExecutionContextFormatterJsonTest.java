package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.QueryExecutionContextBuilder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryInfoBuilder;
import net.ttddyy.dsproxy.StatementType;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryExecutionContextFormatterJsonTest {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    void showAll() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        QueryInfo queryInfo = QueryInfoBuilder.create().query("select 1").build();

        QueryExecutionContext queryExecutionContext = QueryExecutionContextBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.STATEMENT)
                .success(true)
                .batch(false)
                .batchSize(0)
                .queries(Lists.newArrayList(queryInfo))
                .build();

        QueryExecutionContextJsonFormatter formatter = QueryExecutionContextJsonFormatter.showAll();

        String entry = formatter.format(queryExecutionContext);
        assertThat(entry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"time\":100, \"success\":true, \"type\":\"Statement\", \"batch\":false, \"querySize\":1, \"batchSize\":0, \"query\":[\"select 1\"], \"params\":[{}]}");
    }

    @Test
    void showAllForBatchStatement() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        QueryInfo queryInfo1 = QueryInfoBuilder.create().query("select 1").build();
        QueryInfo queryInfo2 = QueryInfoBuilder.create().query("select 2").build();

        QueryExecutionContext queryExecutionContext = QueryExecutionContextBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.STATEMENT)
                .success(true)
                .batch(true)
                .batchSize(2)
                .queries(Lists.newArrayList(queryInfo1, queryInfo2))
                .build();

        QueryExecutionContextJsonFormatter formatter = QueryExecutionContextJsonFormatter.showAll();

        String entry = formatter.format(queryExecutionContext);
        assertThat(entry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"time\":100, \"success\":true, \"type\":\"Statement\", \"batch\":true, \"querySize\":2, \"batchSize\":2, \"query\":[\"select 1\",\"select 2\"], \"params\":[{},{}]}");

    }

    @Test
    void showAllForPreparedStatement() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .param(1, "foo")
                .param(2, 100)
                .param(3, null)  // mimic "setString(3, null)"
                .build();

        QueryExecutionContext queryExecutionContext = QueryExecutionContextBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.PREPARED)
                .success(true)
                .batch(false)
                .batchSize(0)
                .queries(Lists.newArrayList(queryInfo))
                .build();

        QueryExecutionContextJsonFormatter formatter = QueryExecutionContextJsonFormatter.showAll();

        String entry = formatter.format(queryExecutionContext);
        assertThat(entry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"time\":100, \"success\":true, \"type\":\"Prepared\", \"batch\":false, \"querySize\":1, \"batchSize\":0, \"query\":[\"select 1\"], \"params\":[[\"foo\",\"100\",null]]}");

    }

    @Test
    void showAllForBatchPreparedStatement() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .batchParam(1, 1, "foo")
                .batchParam(1, 2, 100)
                .batchParam(2, 1, "bar")
                .batchParam(2, 2, 200)
                .build();

        QueryExecutionContext queryExecutionContext = QueryExecutionContextBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.PREPARED)
                .success(true)
                .batch(true)
                .batchSize(2)
                .queries(Lists.newArrayList(queryInfo))
                .build();

        QueryExecutionContextJsonFormatter formatter = QueryExecutionContextJsonFormatter.showAll();


        String entry = formatter.format(queryExecutionContext);
        assertThat(entry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"time\":100, \"success\":true, \"type\":\"Prepared\", \"batch\":true, \"querySize\":1, \"batchSize\":2, \"query\":[\"select 1\"], \"params\":[[\"foo\",\"100\"],[\"bar\",\"200\"]]}");

    }

    @Test
    void showAllForCallableStatement() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .param("name", "foo")
                .param("id", 100)
                .build();

        QueryExecutionContext queryExecutionContext = QueryExecutionContextBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.CALLABLE)
                .success(true)
                .batch(false)
                .batchSize(0)
                .queries(Lists.newArrayList(queryInfo))
                .build();

        QueryExecutionContextJsonFormatter formatter = QueryExecutionContextJsonFormatter.showAll();

        String entry = formatter.format(queryExecutionContext);
        assertThat(entry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"time\":100, \"success\":true, \"type\":\"Callable\", \"batch\":false, \"querySize\":1, \"batchSize\":0, \"query\":[\"select 1\"], \"params\":[{\"id\":\"100\",\"name\":\"foo\"}]}");

    }

    @Test
    void showAllForBatchCallableStatement() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .batchParam(1, "name", "foo")
                .batchParam(1, "id", 100)
                .batchParam(2, "name", "bar")
                .batchParam(2, "id", 200)
                .build();

        QueryExecutionContext queryExecutionContext = QueryExecutionContextBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.CALLABLE)
                .success(true)
                .batch(true)
                .batchSize(2)
                .queries(Lists.newArrayList(queryInfo))
                .build();

        QueryExecutionContextJsonFormatter formatter = QueryExecutionContextJsonFormatter.showAll();


        String entry = formatter.format(queryExecutionContext);
        assertThat(entry).isEqualTo("{\"name\":\"foo\", \"connection\":10, \"time\":100, \"success\":true, \"type\":\"Callable\", \"batch\":true, \"querySize\":1, \"batchSize\":2, \"query\":[\"select 1\"], \"params\":[{\"id\":\"100\",\"name\":\"foo\"},{\"id\":\"200\",\"name\":\"bar\"}]}");

    }

    @Test
    void showParametersOrderByIndex() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .batchParam(1, 2, 100)  // batch 1, index 2
                .batchParam(1, 3, "FOO")  // batch 1, index 3
                .batchParam(1, 1, "foo")  // batch 1, index 1
                .batchParam(2, 1, "bar")  // batch 2, index 1
                .batchParam(2, 3, "BAR")  // batch 2, index 3
                .batchParam(2, 2, 200)  // batch 2, index 2
                .build();

        QueryExecutionContext queryExecutionContext = QueryExecutionContextBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.PREPARED)
                .success(true)
                .batch(true)
                .batchSize(2)
                .queries(Lists.newArrayList(queryInfo))
                .build();


        QueryExecutionContextJsonFormatter formatter = new QueryExecutionContextJsonFormatter();
        formatter.showParameters();

        String entry = formatter.format(queryExecutionContext);
        assertThat(entry).isEqualTo("{\"params\":[[\"foo\",\"100\",\"FOO\"],[\"bar\",\"200\",\"BAR\"]]}");

    }

    @Test
    void showParametersOrderByNamedParam() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        QueryInfo queryInfo = QueryInfoBuilder.create()
                .query("select 1")
                .batchParam(1, "c-idx", 100)
                .batchParam(1, "b-idx", "FOO")
                .batchParam(1, "a-idx", "foo")
                .batchParam(2, "b-idx", "BAR")
                .batchParam(2, "a-idx", "bar")
                .batchParam(2, "c-idx", 200)
                .build();

        QueryExecutionContext queryExecutionContext = QueryExecutionContextBuilder
                .create()
                .dataSourceName("foo")
                .connectionId("10")
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.CALLABLE)
                .success(true)
                .batch(true)
                .batchSize(2)
                .queries(Lists.newArrayList(queryInfo))
                .build();

        QueryExecutionContextJsonFormatter formatter = new QueryExecutionContextJsonFormatter();
        formatter.showParameters();

        String entry = formatter.format(queryExecutionContext);
        assertThat(entry).isEqualTo("{\"params\":[{\"a-idx\":\"foo\",\"b-idx\":\"FOO\",\"c-idx\":\"100\"},{\"a-idx\":\"bar\",\"b-idx\":\"BAR\",\"c-idx\":\"200\"}]}");

    }

    @Test
    void showDuration() {
        QueryExecutionContext queryExecutionContext;
        String result;

        QueryExecutionContextJsonFormatter formatter = new QueryExecutionContextJsonFormatter();
        formatter.showDuration();

        queryExecutionContext = QueryExecutionContextBuilder.create().elapsedTime(23).build();
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"time\":23}");
    }

    @Test
    void showStatementType() {
        QueryExecutionContext queryExecutionContext;
        String result;

        QueryExecutionContextJsonFormatter formatter = new QueryExecutionContextJsonFormatter();
        formatter.showStatementType();


        // Statement
        queryExecutionContext = QueryExecutionContextBuilder.create().statementType(StatementType.STATEMENT).build();
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"type\":\"Statement\"}");

        // PreparedStatement
        queryExecutionContext = QueryExecutionContextBuilder.create().statementType(StatementType.PREPARED).build();
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"type\":\"Prepared\"}");

        // CallableStatement
        queryExecutionContext = QueryExecutionContextBuilder.create().statementType(StatementType.CALLABLE).build();
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"type\":\"Callable\"}");
    }

    @Test
    void showQueries() {
        QueryExecutionContext queryExecutionContext = QueryExecutionContextBuilder.create().build();
        QueryInfo select1 = QueryInfoBuilder.create().query("select 1").build();
        QueryInfo select2 = QueryInfoBuilder.create().query("select 2").build();
        QueryInfo select3 = QueryInfoBuilder.create().query("select 3").build();

        QueryExecutionContextJsonFormatter formatter = new QueryExecutionContextJsonFormatter();
        formatter.showQueries();
        String result;

        // single query
        queryExecutionContext.setQueries(Arrays.asList(select1));
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"query\":[\"select 1\"]}");

        // multiple query
        queryExecutionContext.setQueries(Arrays.asList(select1, select2, select3));
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"query\":[\"select 1\",\"select 2\",\"select 3\"]}");
    }

    @Test
    void showQuerySize() {
        QueryExecutionContext queryExecutionContext = QueryExecutionContextBuilder.create().build();
        QueryInfo select1 = QueryInfoBuilder.create().query("select 1").build();
        QueryInfo select2 = QueryInfoBuilder.create().query("select 2").build();
        QueryInfo select3 = QueryInfoBuilder.create().query("select 3").build();

        QueryExecutionContextJsonFormatter formatter = new QueryExecutionContextJsonFormatter();
        formatter.showQuerySize();
        String result;

        // single query
        queryExecutionContext.setQueries(Arrays.asList(select1));
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"querySize\":1}");

        // multiple query
        queryExecutionContext.setQueries(Arrays.asList(select1, select2, select3));
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"querySize\":3}");
    }

    @Test
    void showSuccess() {
        QueryExecutionContext queryExecutionContext;
        String result;

        QueryExecutionContextJsonFormatter formatter = new QueryExecutionContextJsonFormatter();
        formatter.showSuccess();

        // success
        queryExecutionContext = QueryExecutionContextBuilder.create().success(true).build();
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"success\":true}");

        // fail
        queryExecutionContext = QueryExecutionContextBuilder.create().success(false).build();
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"success\":false}");

    }

    @Test
    void showBatch() {
        QueryExecutionContext queryExecutionContext;
        String result;

        QueryExecutionContextJsonFormatter formatter = new QueryExecutionContextJsonFormatter();
        formatter.showBatch();

        // success
        queryExecutionContext = QueryExecutionContextBuilder.create().batch(true).build();
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"batch\":true}");

        // fail
        queryExecutionContext = QueryExecutionContextBuilder.create().batch(false).build();
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"batch\":false}");
    }

    @Test
    void showBatchSize() {
        QueryExecutionContext queryExecutionContext;
        String result;

        QueryExecutionContextJsonFormatter formatter = new QueryExecutionContextJsonFormatter();
        formatter.showBatchSize();

        // default
        queryExecutionContext = QueryExecutionContextBuilder.create().build();
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"batchSize\":0}");

        queryExecutionContext = QueryExecutionContextBuilder.create().batchSize(100).build();
        result = formatter.format(queryExecutionContext);
        assertThat(result).isEqualTo("{\"batchSize\":100}");
    }


    @Test
    void newline() {
        QueryExecutionContextJsonFormatter formatter = new QueryExecutionContextJsonFormatter();
        formatter.newLine();
        formatter.showSuccess();
        formatter.newLine();
        formatter.showSuccess();
        formatter.newLine();

        QueryExecutionContext queryExecutionContext = QueryExecutionContextBuilder.create().build();
        String result = formatter.format(queryExecutionContext);


        assertThat(result).hasLineCount(4);
        String[] lines = result.split(LINE_SEPARATOR);
        assertThat(lines[0]).isEqualTo("{");
        assertThat(lines[1]).isEqualTo("\"success\":false, "); // delimiter is appended
        assertThat(lines[2]).isEqualTo("\"success\":false, "); // delimiter is appended
        assertThat(lines[3]).isEqualTo("}");
    }

}
