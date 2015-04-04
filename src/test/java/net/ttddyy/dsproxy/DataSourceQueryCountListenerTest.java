package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.DataSourceQueryCountListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class DataSourceQueryCountListenerTest {

    private QueryInfo queryInfo;
    private List<QueryInfo> queryInfoList;
    private ExecutionInfo executionInfo;
    private DataSourceQueryCountListener listener;

    @Before
    public void setUp() {
        queryInfo = mock(QueryInfo.class);

        queryInfoList = new ArrayList<QueryInfo>();
        queryInfoList.add(queryInfo);

        executionInfo = mock(ExecutionInfo.class);
        given(executionInfo.getDataSourceName()).willReturn("testDS");
        given(executionInfo.getElapsedTime()).willReturn(123L);


        listener = new DataSourceQueryCountListener();
    }

    @After
    public void tearDown() {
        QueryCountHolder.clear();
    }


    @Test
    public void testSelect() {
        given(queryInfo.getQuery()).willReturn("select * from emp");
        listener.afterQuery(executionInfo, queryInfoList);
        verifyQueryCount(1, 0, 0, 0, 0);
    }

    @Test
    public void testInsert() {
        given(queryInfo.getQuery()).willReturn("insert into emp (id) values (1)");
        listener.afterQuery(executionInfo, queryInfoList);
        verifyQueryCount(0, 1, 0, 0, 0);
    }

    @Test
    public void testUpdate() {
        given(queryInfo.getQuery()).willReturn("update emp set id = 1");
        listener.afterQuery(executionInfo, queryInfoList);
        verifyQueryCount(0, 0, 1, 0, 0);
    }

    @Test
    public void testDelete() {
        given(queryInfo.getQuery()).willReturn("delete * from emp");
        listener.afterQuery(executionInfo, queryInfoList);
        verifyQueryCount(0, 0, 0, 1, 0);
    }

    @Test
    public void testOther() {
        given(queryInfo.getQuery()).willReturn("create table aa(...)");
        listener.afterQuery(executionInfo, queryInfoList);
        verifyQueryCount(0, 0, 0, 0, 1);
    }

    private void verifyQueryCount(int select, int insert, int update, int delete, int other) {
        QueryCount queryCount = QueryCountHolder.get("testDS");
        assertThat(queryCount).isNotNull().isInstanceOf(QueryCount.class);
        assertThat(queryCount.getTime()).as("total time").isEqualTo(123L);
        assertThat(queryCount.getSelect()).as("num of select").isEqualTo(select);
        assertThat(queryCount.getInsert()).as("num of insert").isEqualTo(insert);
        assertThat(queryCount.getUpdate()).as("num of update").isEqualTo(update);
        assertThat(queryCount.getDelete()).as("num of delete").isEqualTo(delete);
        assertThat(queryCount.getOther()).as("num of other").isEqualTo(other);
    }


}
