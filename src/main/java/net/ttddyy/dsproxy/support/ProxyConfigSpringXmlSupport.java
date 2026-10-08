package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.listener.ChainListener;
import net.ttddyy.dsproxy.listener.CompositeMethodListener;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.QueryTransformer;

/**
 * Support for creating a {@link ProxyConfig} bean from XML-based Spring configuration.
 *
 * <p>In an XML-based Spring configuration file, defining a {@link ProxyConfig} bean with its builder class requires
 * extra effort because not all builder methods are JavaBean setters. This class simplifies the process by providing
 * setters for creating a {@link ProxyConfig} bean.
 *
 * <p>Example Spring XML configuration:
 * <pre>
 * {@code
 * <bean id="proxyConfig"
 *       factory-bean="proxyConfigSupport"
 *       factory-method="create"/>
 *
 * <bean id="proxyConfigSupport" class="net.ttddyy.dsproxy.support.ProxyConfigSpringXmlSupport">
 *   <property name="dataSourceName" value="my-ds"/>
 *   <property name="queryListener" ref="myQueryListener"/>
 *   <property name="methodListener" ref="myMethodListener"/>
 * </bean>
 *
 * <bean id="myQueryListener" class="net.ttddyy.dsproxy.listener.ChainListener">
 *   <property name="listeners">
 *     <list>
 *       <bean class="net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener"/>
 *     </list>
 *   </property>
 * </bean>
 *
 * <bean id="myMethodListener" class="net.ttddyy.dsproxy.listener.CompositeMethodListener">
 *   <property name="listeners">
 *       <list>
 *       </list>
 *   </property>
 * </bean>
 * }
 * </pre>
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.4
 */
public class ProxyConfigSpringXmlSupport {

    private String dataSourceName;
    private ChainListener queryListener;
    private QueryTransformer queryTransformer;
    private ParameterTransformer parameterTransformer;
    private JdbcProxyFactory jdbcProxyFactory;
    private ResultSetProxyLogicFactory resultSetProxyLogicFactory;
    private ConnectionIdManager connectionIdManager;
    private CompositeMethodListener methodListener;

    public ProxyConfig create() {
        ProxyConfig.Builder builder = ProxyConfig.Builder.create();
        if (this.dataSourceName != null) {
            builder.dataSourceName(this.dataSourceName);
        }
        if (this.queryListener != null) {
            builder.queryListener(this.queryListener);
        }
        if (this.queryTransformer != null) {
            builder.queryTransformer(this.queryTransformer);
        }
        if (this.parameterTransformer != null) {
            builder.parameterTransformer(this.parameterTransformer);
        }
        if (this.jdbcProxyFactory != null) {
            builder.jdbcProxyFactory(this.jdbcProxyFactory);
        }
        if (this.resultSetProxyLogicFactory != null) {
            builder.resultSetProxyLogicFactory(this.resultSetProxyLogicFactory);
        }
        if (this.connectionIdManager != null) {
            builder.connectionIdManager(this.connectionIdManager);
        }
        if (this.methodListener != null) {
            builder.methodListener(this.methodListener);
        }
        return builder.build();
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public void setQueryListener(ChainListener queryListener) {
        this.queryListener = queryListener;
    }

    public void setQueryTransformer(QueryTransformer queryTransformer) {
        this.queryTransformer = queryTransformer;
    }

    public void setParameterTransformer(ParameterTransformer parameterTransformer) {
        this.parameterTransformer = parameterTransformer;
    }

    public void setJdbcProxyFactory(JdbcProxyFactory jdbcProxyFactory) {
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public void setResultSetProxyLogicFactory(ResultSetProxyLogicFactory resultSetProxyLogicFactory) {
        this.resultSetProxyLogicFactory = resultSetProxyLogicFactory;
    }

    public void setConnectionIdManager(ConnectionIdManager connectionIdManager) {
        this.connectionIdManager = connectionIdManager;
    }

    public void setMethodListener(CompositeMethodListener methodListener) {
        this.methodListener = methodListener;
    }
}
