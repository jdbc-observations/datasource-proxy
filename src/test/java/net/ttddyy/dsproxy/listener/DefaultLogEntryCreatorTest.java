package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.*;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class DefaultLogEntryCreatorTest {

    @Test
    public void getLogEntry() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        ExecutionInfo executionInfo = ExecutionInfoBuilder
                .create()
                .dataSourceName("foo")
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.STATEMENT)
                .success(true)
                .batch(true)
                .batchSize(2)
                .build();

        QueryInfo queryInfo = QueryInfoBuilder.create().query("select 1").build();

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();
        String entry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true);


        assertThat(entry).isEqualTo("Name:foo, Time:100, Success:True, Type:Statement, Batch:True, QuerySize:1, BatchSize:2, Query:[\"select 1\"], Params:[()]");
    }

    @Test
    public void statementType() throws Exception {
        ExecutionInfo executionInfo;
        String result;

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();

        // Statement
        executionInfo = ExecutionInfoBuilder.create().statementType(StatementType.STATEMENT).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Type:Statement");

        // PreparedStatement
        executionInfo = ExecutionInfoBuilder.create().statementType(StatementType.PREPARED).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Type:Prepared");

        // CallableStatement
        executionInfo = ExecutionInfoBuilder.create().statementType(StatementType.CALLABLE).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Type:Callable");
    }

    @Test
    public void query() throws Exception {
        ExecutionInfo executionInfo = ExecutionInfoBuilder.create().build();
        QueryInfo select1 = QueryInfoBuilder.create().query("select 1").build();
        QueryInfo select2 = QueryInfoBuilder.create().query("select 2").build();
        QueryInfo select3 = QueryInfoBuilder.create().query("select 3").build();

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();
        String result;

        // single query
        result = creator.getLogEntry(executionInfo, Arrays.asList(select1), true);
        assertThat(result).containsOnlyOnce("Query:[\"select 1\"]");

        // multiple query
        result = creator.getLogEntry(executionInfo, Arrays.asList(select1, select2, select3), true);
        assertThat(result).containsOnlyOnce("Query:[\"select 1\",\"select 2\",\"select 3\"]");
    }

    @Test
    public void querySize() throws Exception {
        ExecutionInfo executionInfo = ExecutionInfoBuilder.create().build();
        QueryInfo select1 = QueryInfoBuilder.create().query("select 1").build();
        QueryInfo select2 = QueryInfoBuilder.create().query("select 2").build();
        QueryInfo select3 = QueryInfoBuilder.create().query("select 3").build();

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();
        String result;

        // single query
        result = creator.getLogEntry(executionInfo, Arrays.asList(select1), true);
        assertThat(result).containsOnlyOnce("QuerySize:1");

        // multiple query
        result = creator.getLogEntry(executionInfo, Arrays.asList(select1, select2, select3), true);
        assertThat(result).containsOnlyOnce("QuerySize:3");
    }

    @Test
    public void success() throws Exception {
        ExecutionInfo executionInfo;
        String result;

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();

        // success
        executionInfo = ExecutionInfoBuilder.create().success(true).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Success:True");

        // fail
        executionInfo = ExecutionInfoBuilder.create().success(false).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Success:False");

    }

    @Test
    public void batch() throws Exception {
        ExecutionInfo executionInfo;
        String result;

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();

        // success
        executionInfo = ExecutionInfoBuilder.create().batch(true).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Batch:True");

        // fail
        executionInfo = ExecutionInfoBuilder.create().batch(false).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Batch:False");
    }

    @Test
    public void batchSize() throws Exception {
        ExecutionInfo executionInfo;
        String result;

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();

        // default
        executionInfo = ExecutionInfoBuilder.create().build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("BatchSize:0");

        executionInfo = ExecutionInfoBuilder.create().batchSize(100).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("BatchSize:100");
    }

}
