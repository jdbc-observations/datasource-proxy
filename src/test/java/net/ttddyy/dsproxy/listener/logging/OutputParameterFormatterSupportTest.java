package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.ExecutionInfoBuilder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.proxy.ParameterKey;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import net.ttddyy.dsproxy.proxy.ParameterSetOperations;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

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
 * @since 2.0
 */
public class OutputParameterFormatterSupportTest {

    @Test
    public void outParams() throws Exception {
        // prepare QueryInfo

        ParameterSetOperation param1 = new ParameterSetOperation();
        param1.setMethod(CallableStatement.class.getMethod("registerOutParameter", int.class, int.class));
        param1.setParameterKey(new ParameterKey(1));
        param1.setArgs(new Object[]{1, 10});

        ParameterSetOperation param2 = new ParameterSetOperation();
        param2.setMethod(CallableStatement.class.getMethod("registerOutParameter", String.class, int.class));
        param2.setParameterKey(new ParameterKey("foo"));
        param2.setArgs(new Object[]{"foo", 11});

        ParameterSetOperation param3 = new ParameterSetOperation();
        param3.setMethod(CallableStatement.class.getMethod("registerOutParameter", int.class, int.class));
        param3.setParameterKey(new ParameterKey(2));
        param3.setArgs(new Object[]{2, 20});

        ParameterSetOperation param4 = new ParameterSetOperation();
        param4.setMethod(CallableStatement.class.getMethod("registerOutParameter", String.class, int.class));
        param4.setParameterKey(new ParameterKey("bar"));
        param4.setArgs(new Object[]{"bar", 21});

        // first batch
        ParameterSetOperations params1 = new ParameterSetOperations();
        params1.add(param1);
        params1.add(param2);

        // second batch
        ParameterSetOperations params2 = new ParameterSetOperations();
        params2.add(param3);
        params2.add(param4);

        List<ParameterSetOperations> parameterSetOperations = new ArrayList<>(Arrays.asList(params1, params2));


        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery("select 1");
        queryInfo.getParameterSetOperations().addAll(parameterSetOperations);


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
                .queries(Lists.newArrayList(queryInfo))
                .build();
        executionInfo.setStatement(cs);   // add mock statement


        ExecutionInfoFormatter formatter = new ExecutionInfoFormatter();
        formatter.addConsumer(OutputParameterFormatterSupport.onOutputParameter);

        String entry = formatter.format(executionInfo);
        assertThat(entry).isEqualTo("OutParams:[(1=100,foo=101),(2=200,bar=201)]");

    }
}
