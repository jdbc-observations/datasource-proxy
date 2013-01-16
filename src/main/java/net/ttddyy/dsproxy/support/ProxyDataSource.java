package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.proxy.JdbcProxyFactory;
import net.ttddyy.dsproxy.proxy.JdkJdbcProxyFactory;

import javax.sql.DataSource;
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
public class ProxyDataSource implements DataSource {
    private DataSource dataSource;
    private QueryExecutionListener listener;
    private String dataSourceName = "";
    private JdbcProxyFactory jdbcProxyFactory = JdbcProxyFactory.DEFAULT;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    public Connection getConnection() throws SQLException {
        final Connection conn = dataSource.getConnection();
        return getConnectionProxy(conn);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        final Connection conn = dataSource.getConnection(username, password);
        return getConnectionProxy(conn);
    }

    private Connection getConnectionProxy(Connection conn) {
        return jdbcProxyFactory.createConnection(conn, listener, dataSourceName);
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

    public void setListener(QueryExecutionListener listener) {
        this.listener = listener;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();  // JDBC4.1 (jdk7+)
    }

    public JdbcProxyFactory getJdbcProxyFactory() {
        return jdbcProxyFactory;
    }

    public void setJdbcProxyFactory(JdbcProxyFactory jdbcProxyFactory) {
        this.jdbcProxyFactory = jdbcProxyFactory;
    }
}
