package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class MockTestUtils {
    @SuppressWarnings("unchecked")
    public static void verifyListenerForBatch(QueryExecutionListener listener, String dataSourceName, String query,
                                              Object[]... expectedQueryArgsList) {
        ArgumentCaptor<ExecutionInfo> executionInfoCaptor = ArgumentCaptor.forClass(ExecutionInfo.class);
        ArgumentCaptor<List> queryInfoListCaptor = ArgumentCaptor.forClass(List.class);

        verify(listener).afterQuery(executionInfoCaptor.capture(), queryInfoListCaptor.capture());

        ExecutionInfo execInfo = executionInfoCaptor.getValue();
        assertThat(execInfo.getMethod(), is(notNullValue()));
        assertThat(execInfo.getMethod().getName(), is("executeBatch"));

        assertThat(execInfo.getMethodArgs(), is(nullValue()));
        assertThat(execInfo.getDataSourceName(), is(dataSourceName));
        assertThat(execInfo.getThrowable(), is(nullValue()));
        assertThat(execInfo.isBatch(), is(true));
        assertThat(execInfo.getBatchSize(), is(expectedQueryArgsList.length));

        List<QueryInfo> queryInfoList = queryInfoListCaptor.getValue();
        assertThat("for prepared/callable statement, batch query size is always 1", queryInfoList.size(), is(1));
        QueryInfo queryInfo = queryInfoList.get(0);
        assertThat(queryInfo.getQuery(), is(query));

        final int expectedBatchSize = expectedQueryArgsList.length;
        assertThat(queryInfo.getQueryArgsList(), hasSize(expectedBatchSize));

        for (int i = 0; i < expectedBatchSize; i++) {
            Object[] expectedQueryArgs = expectedQueryArgsList[i];
            List<?> actualQueryArgs = queryInfo.getQueryArgsList().get(i);

            int expectedQueryArgsSize = expectedQueryArgs.length;
            assertThat(actualQueryArgs, hasSize(expectedQueryArgsSize));

            for (int j = 0; j < expectedQueryArgs.length; j++) {
                Object value = actualQueryArgs.get(j);
                Object expected = expectedQueryArgs[j];

                assertThat(value, is(expected));
            }

        }

        // TODO: change
//        for (int i = 0; i < expectedBatchSize; i++) {
//            Object[] queryArgs = expectedQueryArgsList[i];
//
//            verifyQueryInfo(queryInfo, query, queryArgs);
//        }

    }

    private static void verifyQueryInfo(QueryInfo queryInfo, String query, Object... args) {
        assertThat(queryInfo.getQuery(), is(equalTo(query)));

        List<?> queryArgs = queryInfo.getQueryArgsList();
        assertThat(queryArgs.size(), is(args.length));

        for (int i = 0; i < queryArgs.size(); i++) {
            Object value = queryArgs.get(i);
            Object expected = args[i];

            assertThat(value, is(expected));
        }

    }

}
