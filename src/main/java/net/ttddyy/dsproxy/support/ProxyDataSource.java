package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.ConnectionIdManager;
import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.ConnectionAcquiringListener;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.InterceptorHolder;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.transform.QueryTransformer;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
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
    private DataSource dataSource;
    private InterceptorHolder interceptorHolder = new InterceptorHolder();  // default
    private String dataSourceName = "";
    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;
    private ConnectionIdManager connectionIdManager = ConnectionIdManager.DEFAULT;

    public ProxyDataSource() {
    }

    public ProxyDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    public Connection getConnection() throws SQLException {
        final ConnectionAcquiringListener listener = interceptorHolder.getConnectionAcquiringListener();

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName(this.dataSourceName);

        listener.beforeAcquireConnection(connectionInfo);

        final long beforeTime = System.currentTimeMillis();
        try {
            final Connection conn = dataSource.getConnection();

            final long elapsedTime = System.currentTimeMillis() - beforeTime;

            long connectionId = this.connectionIdManager.getId(conn);
            connectionInfo.setConnectionId(connectionId);

            listener.afterAcquireConnection(connectionInfo, elapsedTime, null);

            return jdbcProxyFactory.createConnection(conn, interceptorHolder, connectionInfo);
        }
        catch (RuntimeException e) {
            final long elapsedTime = System.currentTimeMillis() - beforeTime;
            listener.afterAcquireConnection(connectionInfo, elapsedTime, e);
            throw e;
        }
        catch (SQLException e) {
            final long elapsedTime = System.currentTimeMillis() - beforeTime;
            listener.afterAcquireConnection(connectionInfo, elapsedTime, e);
            throw e;
        }
    }

    public Connection getConnection(String username, String password) throws SQLException {
        final ConnectionAcquiringListener listener = interceptorHolder.getConnectionAcquiringListener();

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName(this.dataSourceName);

        listener.beforeAcquireConnection(connectionInfo);

        final long beforeTime = System.currentTimeMillis();
        try {
            final Connection conn = dataSource.getConnection(username, password);

            final long elapsedTime = System.currentTimeMillis() - beforeTime;

            long connectionId = this.connectionIdManager.getId(conn);
            connectionInfo.setConnectionId(connectionId);

            listener.afterAcquireConnection(connectionInfo, elapsedTime, null);

            return jdbcProxyFactory.createConnection(conn, interceptorHolder, connectionInfo);
        }
        catch (RuntimeException e) {
            final long elapsedTime = System.currentTimeMillis() - beforeTime;
            listener.afterAcquireConnection(connectionInfo, elapsedTime, e);
            throw e;
        }
        catch (SQLException e) {
            final long elapsedTime = System.currentTimeMillis() - beforeTime;
            listener.afterAcquireConnection(connectionInfo, elapsedTime, e);
            throw e;
        }
    }

    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        dataSource.setLogWriter(printWriter);
    }

    public void setLoginTimeout(int i) throws SQLException {
        dataSource.setLoginTimeout(i);
    }

    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    public <T> T unwrap(Class<T> tClass) throws SQLException {
        return dataSource.unwrap(tClass);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }

    /**
     * Set {@link QueryExecutionListener} with default(NoOp) {@link QueryTransformer}.
     *
     * @param listener a lister
     * @deprecated
     */
    public void setListener(QueryExecutionListener listener) {
        this.interceptorHolder = new InterceptorHolder(listener, QueryTransformer.DEFAULT);
    }

    public void addListener(QueryExecutionListener listener) {
        this.interceptorHolder.addListener(listener);
    }

    public void addConnectionAcquiringListener(ConnectionAcquiringListener listener) {
        this.interceptorHolder.addConnectionAcquiringListener(listener);
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    @IgnoreJRERequirement
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();  // JDBC4.1 (jdk7+)
    }

    public JdbcProxyFactory getJdbcProxyFactory() {
        return jdbcProxyFactory;
    }

    public void setJdbcProxyFactory(JdbcProxyFactory jdbcProxyFactory) {
        this.jdbcProxyFactory = jdbcProxyFactory;
    }

    public InterceptorHolder getInterceptorHolder() {
        return interceptorHolder;
    }

    public void setInterceptorHolder(InterceptorHolder interceptorHolder) {
        this.interceptorHolder = interceptorHolder;
    }

    /**
     * @since 1.4.2
     */
    public ConnectionIdManager getConnectionIdManager() {
        return connectionIdManager;
    }


    /**
     * @since 1.4.2
     */
    public void setConnectionIdManager(ConnectionIdManager connectionIdManager) {
        this.connectionIdManager = connectionIdManager;
    }

    @Override
    public void close() throws IOException {
        if (dataSource instanceof Closeable) {
            ((Closeable) dataSource).close();
        }
    }
}
