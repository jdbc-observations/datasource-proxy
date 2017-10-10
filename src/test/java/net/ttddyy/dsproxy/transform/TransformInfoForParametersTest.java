package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Tadaya Tsuyukubo
 */
public class TransformInfoForParametersTest {

    private TransformInfo transformInfo; // work around to pass obj in inner class

    @Before
    public void setUp() {
        this.transformInfo = null;
    }

    private ParameterTransformer getMockParameterTransformer() {
        ParameterTransformer parameterTransformer = mock(ParameterTransformer.class);
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                // store called TransformInfo object to instance variable
                TransformInfoForParametersTest.this.transformInfo = (TransformInfo) invocation.getArguments()[1];
                return null;
            }
        }).when(parameterTransformer).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));
        return parameterTransformer;
    }


    @Test
    public void testParamTransformerInPreparedStatement() throws Throwable {

        ParameterTransformer parameterTransformer = getMockParameterTransformer();

        PreparedStatement ps = mock(PreparedStatement.class);
        PreparedStatementProxyLogic proxyLogic = getProxyLogic(ps, parameterTransformer);

        Method method = PreparedStatement.class.getMethod("execute");
        Object[] args = new Object[]{};
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, only()).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

        assertThat(this.transformInfo).isNotNull();
        assertThat(this.transformInfo.getClazz()).isAssignableFrom(ps.getClass());
        assertThat(this.transformInfo.getDataSourceName()).isEqualTo("my-ds");
        assertThat(this.transformInfo.getQuery()).isEqualTo("my-query");
        assertThat(this.transformInfo.isBatch()).isFalse();
        assertThat(this.transformInfo.getCount()).isEqualTo(0);

    }

    @Test
    public void testParamTransformerBatchInPreparedStatement() throws Throwable {

        ParameterTransformer parameterTransformer = getMockParameterTransformer();

        PreparedStatement ps = mock(PreparedStatement.class);
        PreparedStatementProxyLogic proxyLogic = getProxyLogic(ps, parameterTransformer);

        Method method = PreparedStatement.class.getMethod("addBatch");
        Object[] args = new Object[]{};

        // first batch invocation
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, times(1)).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));
        assertThat(this.transformInfo).isNotNull();
        assertThat(this.transformInfo.getClazz()).isAssignableFrom(ps.getClass());
        assertThat(this.transformInfo.getDataSourceName()).isEqualTo("my-ds");
        assertThat(this.transformInfo.getQuery()).isEqualTo("my-query");
        assertThat(this.transformInfo.isBatch()).isTrue();
        assertThat(this.transformInfo.getCount()).isEqualTo(0);

        // second batch invocation
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, times(2)).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));
        assertThat(this.transformInfo).isNotNull();
        assertThat(this.transformInfo.getClazz()).isAssignableFrom(ps.getClass());
        assertThat(this.transformInfo.getDataSourceName()).isEqualTo("my-ds");
        assertThat(this.transformInfo.getQuery()).isEqualTo("my-query");
        assertThat(this.transformInfo.isBatch()).isTrue();
        assertThat(this.transformInfo.getCount()).isEqualTo(1);
    }

    @Test
    public void testParamTransformerInCallableStatement() throws Throwable {

        ParameterTransformer parameterTransformer = getMockParameterTransformer();

        CallableStatement cs = mock(CallableStatement.class);
        PreparedStatementProxyLogic proxyLogic = getProxyLogic(cs, parameterTransformer);

        Method method = PreparedStatement.class.getMethod("execute");
        Object[] args = new Object[]{};
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, only()).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

        assertThat(this.transformInfo).isNotNull();
        assertThat(this.transformInfo.getClazz()).isAssignableFrom(cs.getClass());
        assertThat(this.transformInfo.getDataSourceName()).isEqualTo("my-ds");
        assertThat(this.transformInfo.getQuery()).isEqualTo("my-query");
        assertThat(this.transformInfo.isBatch()).isFalse();
        assertThat(this.transformInfo.getCount()).isEqualTo(0);

    }

    @Test
    public void testParamTransformerBatchInCallableStatement() throws Throwable {

        ParameterTransformer parameterTransformer = getMockParameterTransformer();

        CallableStatement cs = mock(CallableStatement.class);
        PreparedStatementProxyLogic proxyLogic = getProxyLogic(cs, parameterTransformer);

        Method method = PreparedStatement.class.getMethod("addBatch");
        Object[] args = new Object[]{};

        // first batch invocation
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, times(1)).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

        assertThat(this.transformInfo).isNotNull();
        assertThat(this.transformInfo.getClazz()).isAssignableFrom(cs.getClass());
        assertThat(this.transformInfo.getDataSourceName()).isEqualTo("my-ds");
        assertThat(this.transformInfo.getQuery()).isEqualTo("my-query");
        assertThat(this.transformInfo.isBatch()).isTrue();
        assertThat(this.transformInfo.getCount()).isEqualTo(0);

        // second batch invocation
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, times(2)).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));
        assertThat(this.transformInfo).isNotNull();
        assertThat(this.transformInfo.getClazz()).isAssignableFrom(cs.getClass());
        assertThat(this.transformInfo.getDataSourceName()).isEqualTo("my-ds");
        assertThat(this.transformInfo.getQuery()).isEqualTo("my-query");
        assertThat(this.transformInfo.isBatch()).isTrue();
        assertThat(this.transformInfo.getCount()).isEqualTo(1);

    }


    private PreparedStatementProxyLogic getProxyLogic(PreparedStatement ps, ParameterTransformer parameterTransformer) {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("my-ds");

        ProxyConfig proxyConfig = ProxyConfig.Builder.create().parameterTransformer(parameterTransformer).build();

        return PreparedStatementProxyLogic.Builder.create()
                .preparedStatement(ps)
                .query("my-query")
                .connectionInfo(connectionInfo)
                .proxyConnection(null)
                .proxyConfig(proxyConfig)
                .build();
    }
}
