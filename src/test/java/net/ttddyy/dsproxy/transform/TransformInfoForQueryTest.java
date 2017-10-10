package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.jdk.ConnectionInvocationHandler;
import net.ttddyy.dsproxy.proxy.jdk.StatementInvocationHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tadaya Tsuyukubo
 */
public class TransformInfoForQueryTest {

    private TransformInfo transformInfo;

    @Before
    public void setUp() {
        transformInfo = null;
    }

    private QueryTransformer getMockQueryTransformer(int timesToAnswer) {
        Answer<String> answer = new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                transformInfo = (TransformInfo) invocation.getArguments()[0];
                return "modified-query";
            }
        };

        QueryTransformer queryTransformer = mock(QueryTransformer.class);
        for (int i = 0; i < timesToAnswer; i++) {
            when(queryTransformer.transformQuery(isA(TransformInfo.class))).then(answer);
        }

        return queryTransformer;
    }

    @Test
    public void testQueryTransformerInStatement() throws Throwable {

        Statement stmt = mock(Statement.class);
        QueryTransformer queryTransformer = getMockQueryTransformer(1);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().queryTransformer(queryTransformer).build();

        StatementInvocationHandler handler = new StatementInvocationHandler(stmt, getConnectionInfo(), null, proxyConfig);

        Method method = Statement.class.getMethod("execute", String.class);
        Object[] args = new Object[]{"my-query"};
        handler.invoke(null, method, args);

        verify(queryTransformer).transformQuery(isA(TransformInfo.class));

        assertThat(transformInfo).isNotNull();
        assertThat(transformInfo.getClazz()).isEqualTo(Statement.class);
        assertThat(transformInfo.getQuery()).isEqualTo("my-query");
        assertThat(transformInfo.getDataSourceName()).isEqualTo("my-ds");
        assertThat(transformInfo.isBatch()).isFalse();
        assertThat(transformInfo.getCount()).isEqualTo(0);

    }


    @Test
    public void testQueryTransformerBatchInStatement() throws Throwable {

        Statement stmt = mock(Statement.class);
        QueryTransformer queryTransformer = getMockQueryTransformer(2);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().queryTransformer(queryTransformer).build();

        StatementInvocationHandler handler = new StatementInvocationHandler(stmt, getConnectionInfo(), null, proxyConfig);

        Method method = Statement.class.getMethod("addBatch", String.class);

        // first batch
        handler.invoke(null, method, new Object[]{"my-query-1"});

        verify(queryTransformer).transformQuery(isA(TransformInfo.class));
        assertThat(transformInfo).isNotNull();
        assertThat(transformInfo.getClazz()).isEqualTo(Statement.class);
        assertThat(transformInfo.getQuery()).isEqualTo("my-query-1");
        assertThat(transformInfo.getDataSourceName()).isEqualTo("my-ds");
        assertThat(transformInfo.isBatch()).isTrue();
        assertThat(transformInfo.getCount()).isEqualTo(0);

        // second batch
        handler.invoke(null, method, new Object[]{"my-query-2"});

        verify(queryTransformer, times(2)).transformQuery(isA(TransformInfo.class));
        assertThat(transformInfo).isNotNull();
        assertThat(transformInfo.getClazz()).isEqualTo(Statement.class);
        assertThat(transformInfo.getQuery()).isEqualTo("my-query-2");
        assertThat(transformInfo.getDataSourceName()).isEqualTo("my-ds");
        assertThat(transformInfo.isBatch()).isTrue();
        assertThat(transformInfo.getCount()).isEqualTo(1);

    }

    @Test
    public void testQueryTransformerInConnectionHandlerForPrepareStatement() throws Throwable {

        Connection conn = mock(Connection.class);
        QueryTransformer queryTransformer = getMockQueryTransformer(1);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().queryTransformer(queryTransformer).build();

        ConnectionInvocationHandler handler = new ConnectionInvocationHandler(conn, getConnectionInfo(), proxyConfig);

        Method method = Connection.class.getMethod("prepareStatement", String.class);

        handler.invoke(null, method, new Object[]{"my-query"});

        verify(queryTransformer).transformQuery(isA(TransformInfo.class));
        assertThat(transformInfo).isNotNull();
        assertThat(transformInfo.getClazz()).isEqualTo(PreparedStatement.class);
        assertThat(transformInfo.getQuery()).isEqualTo("my-query");
        assertThat(transformInfo.getDataSourceName()).isEqualTo("my-ds");
        assertThat(transformInfo.isBatch()).isFalse();
        assertThat(transformInfo.getCount()).isEqualTo(0);

    }

    @Test
    public void testQueryTransformerInConnectionHandlerForPrepareCall() throws Throwable {

        Connection conn = mock(Connection.class);
        QueryTransformer queryTransformer = getMockQueryTransformer(1);
        ProxyConfig proxyConfig = ProxyConfig.Builder.create().queryTransformer(queryTransformer).build();

        ConnectionInvocationHandler handler = new ConnectionInvocationHandler(conn, getConnectionInfo(), proxyConfig);

        Method method = Connection.class.getMethod("prepareCall", String.class);

        handler.invoke(null, method, new Object[]{"my-query"});

        verify(queryTransformer).transformQuery(isA(TransformInfo.class));
        assertThat(transformInfo).isNotNull();
        assertThat(transformInfo.getClazz()).isEqualTo(CallableStatement.class);
        assertThat(transformInfo.getQuery()).isEqualTo("my-query");
        assertThat(transformInfo.getDataSourceName()).isEqualTo("my-ds");
        assertThat(transformInfo.isBatch()).isFalse();
        assertThat(transformInfo.getCount()).isEqualTo(0);

    }

    private ConnectionInfo getConnectionInfo() {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("my-ds");
        return connectionInfo;
    }
}
