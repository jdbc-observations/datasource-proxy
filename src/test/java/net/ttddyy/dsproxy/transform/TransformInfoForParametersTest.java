package net.ttddyy.dsproxy.transform;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.PreparedStatementProxyLogic;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

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
        InterceptorHolder interceptorHolder = new InterceptorHolder(QueryExecutionListener.DEFAULT, QueryTransformer.DEFAULT, parameterTransformer);

        JdbcProxyFactory jdbcProxyFactory = mock(JdbcProxyFactory.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        PreparedStatementProxyLogic proxyLogic = getProxyLogic(ps, interceptorHolder, jdbcProxyFactory);;

        Method method = PreparedStatement.class.getMethod("execute");
        Object[] args = new Object[]{};
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, only()).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

        assertThat(this.transformInfo, notNullValue());
        assertThat(this.transformInfo.getClazz(), typeCompatibleWith(PreparedStatement.class));
        assertThat(this.transformInfo.getDataSourceName(), is("my-ds"));
        assertThat(this.transformInfo.getQuery(), is("my-query"));
        assertThat(this.transformInfo.isBatch(), is(false));
        assertThat(this.transformInfo.getCount(), is(0));

    }

    @Test
    public void testParamTransformerBatchInPreparedStatement() throws Throwable {

        ParameterTransformer parameterTransformer = getMockParameterTransformer();
        InterceptorHolder interceptorHolder = new InterceptorHolder(QueryExecutionListener.DEFAULT, QueryTransformer.DEFAULT, parameterTransformer);

        JdbcProxyFactory jdbcProxyFactory = mock(JdbcProxyFactory.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        PreparedStatementProxyLogic proxyLogic = getProxyLogic(ps, interceptorHolder, jdbcProxyFactory);;

        Method method = PreparedStatement.class.getMethod("addBatch");
        Object[] args = new Object[]{};

        // first batch invocation
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, times(1)).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));
        assertThat(this.transformInfo, notNullValue());
        assertThat(this.transformInfo.getClazz(), typeCompatibleWith(PreparedStatement.class));
        assertThat(this.transformInfo.getDataSourceName(), is("my-ds"));
        assertThat(this.transformInfo.getQuery(), is("my-query"));
        assertThat(this.transformInfo.isBatch(), is(true));
        assertThat(this.transformInfo.getCount(), is(0));

        // second batch invocation
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, times(2)).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));
        assertThat(this.transformInfo, notNullValue());
        assertThat(this.transformInfo.getClazz(), typeCompatibleWith(PreparedStatement.class));
        assertThat(this.transformInfo.getDataSourceName(), is("my-ds"));
        assertThat(this.transformInfo.getQuery(), is("my-query"));
        assertThat(this.transformInfo.isBatch(), is(true));
        assertThat(this.transformInfo.getCount(), is(1));

    }

    @Test
    public void testParamTransformerInCallableStatement() throws Throwable {

        ParameterTransformer parameterTransformer = getMockParameterTransformer();
        InterceptorHolder interceptorHolder = new InterceptorHolder(QueryExecutionListener.DEFAULT, QueryTransformer.DEFAULT, parameterTransformer);

        JdbcProxyFactory jdbcProxyFactory = mock(JdbcProxyFactory.class);
        CallableStatement cs = mock(CallableStatement.class);
        PreparedStatementProxyLogic proxyLogic = getProxyLogic(cs, interceptorHolder, jdbcProxyFactory);;

        Method method = PreparedStatement.class.getMethod("execute");
        Object[] args = new Object[]{};
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, only()).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));

        assertThat(this.transformInfo, notNullValue());
        assertThat(this.transformInfo.getClazz(), typeCompatibleWith(CallableStatement.class));
        assertThat(this.transformInfo.getDataSourceName(), is("my-ds"));
        assertThat(this.transformInfo.getQuery(), is("my-query"));
        assertThat(this.transformInfo.isBatch(), is(false));
        assertThat(this.transformInfo.getCount(), is(0));

    }

    @Test
    public void testParamTransformerBatchInCallableStatement() throws Throwable {

        ParameterTransformer parameterTransformer = getMockParameterTransformer();
        InterceptorHolder interceptorHolder = new InterceptorHolder(QueryExecutionListener.DEFAULT, QueryTransformer.DEFAULT, parameterTransformer);

        JdbcProxyFactory jdbcProxyFactory = mock(JdbcProxyFactory.class);
        CallableStatement cs = mock(CallableStatement.class);
        PreparedStatementProxyLogic proxyLogic = getProxyLogic(cs, interceptorHolder, jdbcProxyFactory);

        Method method = PreparedStatement.class.getMethod("addBatch");
        Object[] args = new Object[]{};

        // first batch invocation
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, times(1)).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));
        assertThat(this.transformInfo, notNullValue());
        assertThat(this.transformInfo.getClazz(), typeCompatibleWith(CallableStatement.class));
        assertThat(this.transformInfo.getDataSourceName(), is("my-ds"));
        assertThat(this.transformInfo.getQuery(), is("my-query"));
        assertThat(this.transformInfo.isBatch(), is(true));
        assertThat(this.transformInfo.getCount(), is(0));

        // second batch invocation
        proxyLogic.invoke(method, args);

        verify(parameterTransformer, times(2)).transformParameters(isA(ParameterReplacer.class), isA(TransformInfo.class));
        assertThat(this.transformInfo, notNullValue());
        assertThat(this.transformInfo.getClazz(), typeCompatibleWith(CallableStatement.class));
        assertThat(this.transformInfo.getDataSourceName(), is("my-ds"));
        assertThat(this.transformInfo.getQuery(), is("my-query"));
        assertThat(this.transformInfo.isBatch(), is(true));
        assertThat(this.transformInfo.getCount(), is(1));

    }


    private PreparedStatementProxyLogic getProxyLogic(PreparedStatement ps, InterceptorHolder interceptorHolder, JdbcProxyFactory jdbcProxyFactory) {
        return PreparedStatementProxyLogic.Builder.create()
                .setPreparedStatement(ps)
                .setQuery("my-query")
                .setInterceptorHolder(interceptorHolder)
                .setDataSourceName("my-ds")
                .setProxyConnection(null)
                .build();
    }
}
