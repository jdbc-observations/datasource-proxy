package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.ExecutionInfoBuilder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class OutputParameterLogEntryCreatorTest {

    @Test
    public void outParams() throws Exception {
        // prepare QueryInfo

        ParameterSetOperation param1 = new ParameterSetOperation();
        param1.setMethod(CallableStatement.class.getMethod("registerOutParameter", int.class, int.class));
        param1.setArgs(new Object[]{1, 10});

        ParameterSetOperation param2 = new ParameterSetOperation();
        param2.setMethod(CallableStatement.class.getMethod("registerOutParameter", String.class, int.class));
        param2.setArgs(new Object[]{"foo", 11});

        ParameterSetOperation param3 = new ParameterSetOperation();
        param3.setMethod(CallableStatement.class.getMethod("registerOutParameter", int.class, int.class));
        param3.setArgs(new Object[]{2, 20});

        ParameterSetOperation param4 = new ParameterSetOperation();
        param4.setMethod(CallableStatement.class.getMethod("registerOutParameter", String.class, int.class));
        param4.setArgs(new Object[]{"bar", 21});


        List<List<ParameterSetOperation>> paramsList = new ArrayList<List<ParameterSetOperation>>();
        paramsList.add(Arrays.asList(param1, param2));  // first batch
        paramsList.add(Arrays.asList(param3, param4));  // second batch


        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery("select 1");
        queryInfo.setParametersList(paramsList);


        // prepare ExecutionInfo

        Method method = Object.class.getMethod("toString");
        Object result = new Object();

        // mock statement
        CallableStatement cs = mock(CallableStatement.class);
        given(cs.getObject(1)).willReturn(100);
        given(cs.getObject("foo")).willReturn(101);
        given(cs.getObject(2)).willReturn(200);
        given(cs.getObject("bar")).willReturn(201);

        ExecutionInfo executionInfo = ExecutionInfoBuilder
                .create()
                .dataSourceName("foo")
                .elapsedTime(100)
                .method(method)
                .result(result)
                .statementType(StatementType.CALLABLE)
                .success(true)
                .batch(false)
                .batchSize(0)
                .build();
        executionInfo.setStatement(cs);   // add mock statement


        OutputParameterLogEntryCreator creator = new OutputParameterLogEntryCreator();

        String entry = creator.getLogEntry(executionInfo, Lists.newArrayList(queryInfo), false, true, false);
        assertThat(entry).containsOnlyOnce("OutParams:[(1=100,foo=101),(2=200,bar=201)]");

    }
}
