package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.DataSourceProxyException;
import net.ttddyy.dsproxy.listener.MethodExecutionListenerUtils;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * A proxy of {@link javax.sql.DataSource} with {@link net.ttddyy.dsproxy.listener.QueryExecutionListener}.
 *
 * @author Tadaya Tsuyukubo
 */
public class ProxyDataSource implements DataSource, Closeable {

    private static final Method GET_CONNECTION_WITH_NO_ARGS;
    private static final Method GET_CONNECTION_WITH_USER_PASS;
    private static final boolean isAutoCloseablePresent = isAutoCloseablePresent();

    static {
        try {
            GET_CONNECTION_WITH_NO_ARGS = DataSource.class.getDeclaredMethod("getConnection");
            GET_CONNECTION_WITH_USER_PASS = DataSource.class.getDeclaredMethod("getConnection", String.class, String.class);
        } catch (NoSuchMethodException e) {
            throw new DataSourceProxyException("Failed to find getConnection methods", e);
        }
    }

    private static boolean isAutoCloseablePresent() {
        try {
            Class.forName("java.lang.AutoCloseable");  // jdk7+
        } catch (ClassNotFoundException ex) {
            return false;
        }
        return true;
    }

    private DataSource dataSource;
    private ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();  // default

    public ProxyDataSource() {
    }

    public ProxyDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public Connection getConnection() throws SQLException {
        final Connection conn = dataSource.getConnection();
        return getConnectionProxy(conn, GET_CONNECTION_WITH_NO_ARGS, null);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        final Connection conn = dataSource.getConnection(username, password);
        return getConnectionProxy(conn, GET_CONNECTION_WITH_USER_PASS, new Object[]{username, password});
    }

    private Connection getConnectionProxy(final Connection conn, Method method, Object[] args) throws SQLException {
        String dataSourceName = this.proxyConfig.getDataSourceName();
        ConnectionIdManager connectionIdManager = this.proxyConfig.getConnectionIdManager();
        final JdbcProxyFactory jdbcProxyFactory = this.proxyConfig.getJdbcProxyFactory();

        String connectionId = connectionIdManager.getId(conn);

        final ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setConnectionId(connectionId);
        connectionInfo.setIsolationLevel(conn.getTransactionIsolation());
        connectionInfo.setDataSourceName(dataSourceName);

        try {
            return (Connection) MethodExecutionListenerUtils.invoke(new MethodExecutionListenerUtils.MethodExecutionCallback() {
                @Override
                public Object execute(Object proxy, Method method, Object[] args) throws Throwable {
                    return jdbcProxyFactory.createConnection(conn, connectionInfo, ProxyDataSource.this.proxyConfig);
                }
            }, this.proxyConfig, this, connectionInfo, method, args);
        } catch (Throwable throwable) {
            if (throwable instanceof SQLException) {
                throw (SQLException) throwable;
            } else {
                throw new DataSourceProxyException("Failed to perform getConnection", throwable);
            }
        }

    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        dataSource.setLogWriter(printWriter);
    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {
        dataSource.setLoginTimeout(i);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException {
        return dataSource.unwrap(tClass);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }

    @IgnoreJRERequirement
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();  // JDBC4.1 (jdk7+)
    }

    @Override
    public void close() throws IOException {
        if (dataSource instanceof Closeable) {
            ((Closeable) dataSource).close();
        } else if (isAutoCloseablePresent && dataSource instanceof AutoCloseable) {
            try {
                ((AutoCloseable) dataSource).close(); // (jdk7+)
            } catch (Exception ex) {
                throw new IOException(ex);
            }
        }
    }

    /**
     * @deprecated
     */
    public void setListener(QueryExecutionListener listener) {
        this.proxyConfig = ProxyConfig.Builder.from(this.proxyConfig)
                .queryListener(listener)
                .build();
    }

    public void addListener(QueryExecutionListener listener) {
        this.proxyConfig.getQueryListener().addListener(listener);
    }

    public void setDataSourceName(String dataSourceName) {
        this.proxyConfig = ProxyConfig.Builder.from(this.proxyConfig)
                .dataSourceName(dataSourceName)
                .build();
    }

    public String getDataSourceName() {
        return this.proxyConfig.getDataSourceName();
    }

    /**
     * @since 1.4.2
     */
    public ConnectionIdManager getConnectionIdManager() {
        return this.proxyConfig.getConnectionIdManager();
    }


    /**
     * @since 1.4.2
     */
    public void setConnectionIdManager(ConnectionIdManager connectionIdManager) {
        this.proxyConfig = ProxyConfig.Builder.from(this.proxyConfig)
                .connectionIdManager(connectionIdManager)
                .build();
    }

    /**
     * @since 1.4.3
     */
    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    /**
     * @since 1.4.3
     */
    public void setProxyConfig(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

}
