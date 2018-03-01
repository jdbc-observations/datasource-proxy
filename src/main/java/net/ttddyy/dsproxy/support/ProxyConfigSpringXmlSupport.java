package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.listener.CompositeProxyDataSourceListener;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import net.ttddyy.dsproxy.proxy.ResultSetProxyLogicFactory;
import net.ttddyy.dsproxy.transform.QueryTransformer;

/**
 * {@link ProxyConfig} bean creation support for XML based spring configuration.
 *
 * In xml based spring configuration file, defining a {@link ProxyConfig} bean with its builder class requires
 * extra effort since all builder methods are not java bean setters.
 * To simplify it, this class provides setters to create a {@link ProxyConfig} bean.
 *
 * <p/>Example spring xml config:
 * <pre>
 * {@code
 * <bean id="proxyConfig"
 *       factory-bean="proxyConfigSupport"
 *       factory-method="create"/>
 *
 * <bean id="proxyConfigSupport" class="net.ttddyy.dsproxy.support.ProxyConfigSpringXmlSupport">
 *   <property name="dataSourceName" value="my-ds"/>
 *   <property name="listener" ref="myListener"/>
 * </bean>
 *
 * <bean id="myListener" class="net.ttddyy.dsproxy.listener.CompositeProxyDataSourceListener">
 *   <property name="listeners">
 *     <list>
 *       <bean class="net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener"/>
 *     </list>
 *   </property>
 * </bean>
 *
 * }
 * </pre>
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.4
 */
public class ProxyConfigSpringXmlSupport {

    private String dataSourceName;
    private CompositeProxyDataSourceListener listener;
    private QueryTransformer queryTransformer;
    private JdbcProxyFactory jdbcProxyFactory;
    private ResultSetProxyLogicFactory resultSetProxyLogicFactory;
    private ConnectionIdManager connectionIdManager;

    public ProxyConfig create() {
        ProxyConfig.Builder builder = ProxyConfig.Builder.create();
        if (this.dataSourceName != null) {
            builder.dataSourceName(this.dataSourceName);
        }
        if (this.listener != null) {
            builder.listener(this.listener);
        }
        if (this.queryTransformer != null) {
            builder.queryTransformer(this.queryTransformer);
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
        return builder.build();
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public void setListener(CompositeProxyDataSourceListener listener) {
        this.listener = listener;
    }

    public void setQueryTransformer(QueryTransformer queryTransformer) {
        this.queryTransformer = queryTransformer;
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

}
