package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.ExecutionInfoBuilder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class DefaultLogEntryCreatorTest {

    @Test
    public void getLogEntry() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        ExecutionInfo executionInfo = new ExecutionInfoBuilder()
                .dataSourceName("foo")
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.STATEMENT)
                .success(true)
                .batch(true)
                .batchSize(2)
                .build();

        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery("select 1");

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();
        String entry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true);


        assertThat(entry).isEqualTo("Name:foo, Time:100, Success:True, Type:Statement, Batch:True, QuerySize:1, BatchSize:2, Query:[(select 1)], Params:[()]");
    }

    @Test
    public void statementType() throws Exception {
        ExecutionInfo executionInfo;
        String result;

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();

        // Statement
        executionInfo = new ExecutionInfoBuilder().statementType(StatementType.STATEMENT).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Type:Statement");

        // PreparedStatement
        executionInfo = new ExecutionInfoBuilder().statementType(StatementType.PREPARED).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Type:Prepared");

        // CallableStatement
        executionInfo = new ExecutionInfoBuilder().statementType(StatementType.CALLABLE).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Type:Callable");
    }

    @Test
    public void success() throws Exception {
        ExecutionInfo executionInfo;
        String result;

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();

        // success
        executionInfo = new ExecutionInfoBuilder().success(true).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Success:True");

        // fail
        executionInfo = new ExecutionInfoBuilder().success(false).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Success:False");

    }

    @Test
    public void batch() throws Exception {
        ExecutionInfo executionInfo;
        String result;

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();

        // success
        executionInfo = new ExecutionInfoBuilder().batch(true).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Batch:True");

        // fail
        executionInfo = new ExecutionInfoBuilder().batch(false).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("Batch:False");
    }

    @Test
    public void batchSize() throws Exception {
        ExecutionInfo executionInfo;
        String result;

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();

        // default
        executionInfo = new ExecutionInfoBuilder().build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("BatchSize:0");

        executionInfo = new ExecutionInfoBuilder().batchSize(100).build();
        result = creator.getLogEntry(executionInfo, new ArrayList<QueryInfo>(), true);
        assertThat(result).containsOnlyOnce("BatchSize:100");
    }

}
