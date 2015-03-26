package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 */
@RunWith(Parameterized.class)
public class DataSourceQueryCountListenerTest {

    @Parameterized.Parameters
    public static Object[][] getSingleQueryData() {
        return new Object[][]{
                // Query, Query Type
                {"select * from emp", QueryType.SELECT},
                {"update emp set id = 1", QueryType.UPDATE},
                {"insert into emp (id) values (1)", QueryType.INSERT},
                {"delete * from emp", QueryType.DELETE},
                {"create table aa(...)", QueryType.OTHER},
        };
    }

    private String query;

    private QueryType expectedType;

    public DataSourceQueryCountListenerTest(String query, QueryType expectedType) {
        this.query = query;
        this.expectedType = expectedType;
    }

    @Before
    public void setup() {
        QueryCountHolder.clear();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSingleQuery() {
        ExecutionInfo executionInfo = mock(ExecutionInfo.class);
        when(executionInfo.getDataSourceName()).thenReturn("testDS");
        when(executionInfo.getElapsedTime()).thenReturn(123L);

        QueryInfo queryInfo = mock(QueryInfo.class);
        when(queryInfo.getQuery()).thenReturn(this.query);

        List<QueryInfo> queryInfoList = new ArrayList<QueryInfo>();
        queryInfoList.add(queryInfo);

        DataSourceQueryCountListener listener = new DataSourceQueryCountListener();
        listener.afterQuery(executionInfo, queryInfoList);

        QueryCount queryCount = QueryCountHolder.get("testDS");

        assertThat(queryCount, is(notNullValue()));
        assertThat(queryCount, is(instanceOf(QueryCount.class)));

        assertThat(queryCount.getCall(), is(equalTo(1)));
        assertThat(queryCount.getTotalNumOfQuery(), is(1));

        if (QueryType.SELECT == this.expectedType) {
            assertThat(queryCount.getSelect(), is(1));
        } else {
            assertThat(queryCount.getSelect(), is(0));
        }
        if (QueryType.UPDATE == expectedType) {
            assertThat(queryCount.getUpdate(), is(1));
        } else {
            assertThat(queryCount.getUpdate(), is(0));
        }
        if (QueryType.INSERT == expectedType) {
            assertThat(queryCount.getInsert(), is(1));
        } else {
            assertThat(queryCount.getInsert(), is(0));
        }
        if (QueryType.DELETE == expectedType) {
            assertThat(queryCount.getDelete(), is(1));
        } else {
            assertThat(queryCount.getDelete(), is(0));
        }
        if (QueryType.OTHER == expectedType) {
            assertThat(queryCount.getOther(), is(1));
        } else {
            assertThat(queryCount.getOther(), is(0));
        }
    }
}
