package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class MockTestUtils {
    @SuppressWarnings("unchecked")
    public static void verifyListenerForBatch(QueryExecutionListener listener, String dataSourceName, String query,
                                              Object[]... expectedQueryArgs) {
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

        final int batchSize = expectedQueryArgs.length;

        List<QueryInfo> queryInfoList = queryInfoListCaptor.getValue();
        assertThat(queryInfoList.size(), is(batchSize));


        for (int i = 0; i < batchSize; i++) {
            QueryInfo queryInfo = queryInfoList.get(i);
            Object[] queryArgs = expectedQueryArgs[i];

            verifyQueryInfo(queryInfo, query, queryArgs);
        }

    }

    private static void verifyQueryInfo(QueryInfo queryInfo, String query, Object... args) {
        assertThat(queryInfo.getQuery(), is(equalTo(query)));

        List<?> queryArgs = queryInfo.getQueryArgs();
        assertThat(queryArgs.size(), is(args.length));

        for (int i = 0; i < queryArgs.size(); i++) {
            Object value = queryArgs.get(i);
            Object expected = args[i];

            assertThat(value, is(expected));
        }

    }

}
