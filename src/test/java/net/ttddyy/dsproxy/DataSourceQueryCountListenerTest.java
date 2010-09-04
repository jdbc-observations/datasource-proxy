package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.QueryType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class DataSourceQueryCountListenerTest {

    @BeforeMethod
    public void setup() {
        QueryCountHolder.clear();
    }

    @DataProvider
    public Object[][] getSingleQueryData() {
        return new Object[][]{
                // Query, Query Type
                {"select * from emp", QueryType.SELECT},
                {"update emp set id = 1", QueryType.UPDATE},
                {"insert into emp (id) values (1)", QueryType.INSERT},
                {"delete * from emp", QueryType.DELETE},
                {"create table aa(...)", QueryType.OTHER},
        };
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "getSingleQueryData")
    public void testSingleQuery(String query, QueryType expectedType) {
        ExecutionInfo executionInfo = mock(ExecutionInfo.class);
        when(executionInfo.getDataSourceName()).thenReturn("testDS");
        when(executionInfo.getElapsedTime()).thenReturn(123L);

        QueryInfo queryInfo = mock(QueryInfo.class);
        when(queryInfo.getQuery()).thenReturn(query);

        List<QueryInfo> queryInfoList = new ArrayList<QueryInfo>();
        queryInfoList.add(queryInfo);

        DataSourceQueryCountListener listener = new DataSourceQueryCountListener();
        listener.afterQuery(executionInfo, queryInfoList);

        QueryCount queryCount = QueryCountHolder.get("testDS");

        assertThat(queryCount, is(notNullValue()));
        assertThat(queryCount, is(instanceOf(QueryCount.class)));

        assertThat(queryCount.getCall(), is(equalTo(1)));
        assertThat(queryCount.getTotalNumOfQuery(), is(1));

        if (QueryType.SELECT == expectedType) {
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
