package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class DefaultLogEntryCreatorTest {

    @Test
    public void getLogEntry() throws Exception {
        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        ExecutionInfo executionInfo = new ExecutionInfo();
        executionInfo.setDataSourceName("foo");
        executionInfo.setElapsedTime(100);
        executionInfo.setMethod(method);
        executionInfo.setResult(result);

        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery("select 1");

        DefaultLogEntryCreator creator = new DefaultLogEntryCreator();
        String entry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), true);


        assertThat(entry).isEqualTo("Name:foo, Time:100, Success:True, QuerySize:1, Query:[(select 1)], Params:[()]");
    }

}
