package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;

/**
 * @author Tadaya Tsuyukubo
 */
public class MockTestUtils {

    // TODO: this needs clean up

    @SuppressWarnings("unchecked")
    public static void verifyListenerForBatch(QueryExecutionListener listener, String dataSourceName, String query,
                                              Map<String, Object>... expectedQueryParamsArray) {
        ArgumentCaptor<ExecutionInfo> executionInfoCaptor = ArgumentCaptor.forClass(ExecutionInfo.class);
        ArgumentCaptor<List> queryInfoListCaptor = ArgumentCaptor.forClass(List.class);

        verify(listener).afterQuery(executionInfoCaptor.capture(), queryInfoListCaptor.capture());


        final int expectedBatchSize = expectedQueryParamsArray.length;

        ExecutionInfo execInfo = executionInfoCaptor.getValue();
        assertThat(execInfo.getMethod(), is(notNullValue()));
        assertThat(execInfo.getMethod().getName(), is("executeBatch"));

        assertThat(execInfo.getMethodArgs(), is(nullValue()));
        assertThat(execInfo.getDataSourceName(), is(dataSourceName));
        assertThat(execInfo.getThrowable(), is(nullValue()));
        assertThat(execInfo.isBatch(), is(true));
        assertThat(execInfo.getBatchSize(), is(expectedBatchSize));

        List<QueryInfo> queryInfoList = queryInfoListCaptor.getValue();
        assertThat("for prepared/callable statement, batch query size is always 1", queryInfoList.size(), is(1));
        QueryInfo queryInfo = queryInfoList.get(0);
        assertThat(queryInfo.getQuery(), is(query));
        assertThat(queryInfo.getParametersList(), hasSize(expectedBatchSize));

        for (int i = 0; i < expectedBatchSize; i++) {
            Map<String, Object> expectedQueryArgs = expectedQueryParamsArray[i];
            Map<String, Object> actualQueryArgs = new HashMap<String, Object>();
            for (ParameterSetOperation operation  : queryInfo.getParametersList().get(i)) {
                Object[] args = operation.getArgs();
                actualQueryArgs.put(args[0].toString(), args[1]);
            }


            assertThat(actualQueryArgs, aMapWithSize(expectedQueryArgs.size()));
            for (Map.Entry<String, Object> entry : expectedQueryArgs.entrySet()) {
                assertThat(actualQueryArgs, hasEntry(entry.getKey(), entry.getValue()));
            }
        }

        // TODO: change
//        for (int i = 0; i < expectedBatchSize; i++) {
//            Object[] queryArgs = expectedQueryArgsList[i];
//
//            verifyQueryInfo(queryInfo, query, queryArgs);
//        }

    }

//    private static void verifyQueryInfo(QueryInfo queryInfo, String query, Object... args) {
//        assertThat(queryInfo.getQuery(), is(equalTo(query)));
//
//        List<?> queryArgs = queryInfo.getQueryArgsList();
//        assertThat(queryArgs.size(), is(args.length));
//
//        for (int i = 0; i < queryArgs.size(); i++) {
//            Object value = queryArgs.get(i);
//            Object expected = args[i];
//
//            assertThat(value, is(expected));
//        }
//
//    }

}
