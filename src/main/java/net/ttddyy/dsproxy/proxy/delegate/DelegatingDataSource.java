package net.ttddyy.dsproxy.proxy.delegate;

import net.ttddyy.dsproxy.DataSourceProxyException;
import net.ttddyy.dsproxy.proxy.DataSourceProxyLogic;
import net.ttddyy.dsproxy.proxy.ProxyJdbcObject;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * A concrete class implementation of {@link DataSource} that delegates to {@link DataSourceProxyLogic}.
 *
 * @author Tadaya Tsuyukubo
 * @see DelegatingJdbcProxyFactory
 * @since 1.5
 */
public class DelegatingDataSource implements DataSource, ProxyJdbcObject {

    private static Method getMethodIfAvailable(String name, Class... parameterTypes) {
        return DelegatingUtils.getMethodIfAvailable(DataSource.class, name, parameterTypes);
    }

    private DataSourceProxyLogic proxyLogic;

    public DelegatingDataSource(DataSourceProxyLogic proxyLogic) {
        this.proxyLogic = proxyLogic;
    }

    @Override
    public Object getTarget() {
        try {
            return this.proxyLogic.invoke(DelegatingUtils.GET_TARGET_METHOD, null);
        } catch (Throwable throwable) {
            throw new DataSourceProxyException("Failed to invoke method: getTarget", throwable);
        }
    }

    private Object invoke(final Method method, final Object... args) throws SQLException {
        return DelegatingUtils.invoke(method, new DelegatingUtils.InvocationCallback() {
            @Override
            public Object invoke() throws Throwable {
                return proxyLogic.invoke(method, args);
            }
        });
    }

    private static final Method GET_CONNECTION_METHOD = getMethodIfAvailable("getConnection");

    @Override
    public Connection getConnection() throws SQLException {
        return (Connection) invoke(GET_CONNECTION_METHOD);
    }

    private static final Method GET_CONNECTION_WITH_STRING_AND_STRING_METHOD = getMethodIfAvailable("getConnection", String.class, String.class);

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return (Connection) invoke(GET_CONNECTION_WITH_STRING_AND_STRING_METHOD, username, password);
    }

    private static final Method UNWRAP_METHOD = getMethodIfAvailable("unwrap", Class.class);

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return (T) invoke(UNWRAP_METHOD, iface);
    }

    private static final Method IS_WRAPPER_FOR_METHOD = getMethodIfAvailable("isWrapperFor", Class.class);

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (Boolean) invoke(IS_WRAPPER_FOR_METHOD, iface);
    }

    private static final Method GET_LOG_WRITER_METHOD = getMethodIfAvailable("getLogWriter");

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return (PrintWriter) invoke(GET_LOG_WRITER_METHOD);
    }

    private static final Method SET_LOG_WRITER_METHOD = getMethodIfAvailable("setLogWriter", PrintWriter.class);

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        invoke(SET_LOG_WRITER_METHOD, out);
    }

    private static final Method SET_LOGIN_TIMEOUT_METHOD = getMethodIfAvailable("setLoginTimeout", int.class);

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        invoke(SET_LOGIN_TIMEOUT_METHOD, seconds);
    }

    private static final Method GET_LOGIN_TIMEOUT_METHOD = getMethodIfAvailable("getLoginTimeout");

    @Override
    public int getLoginTimeout() throws SQLException {
        return (Integer) invoke(GET_LOGIN_TIMEOUT_METHOD);
    }

    private static final Method GET_PARENT_LOGGER_METHOD = getMethodIfAvailable("getParentLogger");

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        Method method = GET_PARENT_LOGGER_METHOD;

        if (method == null) {
            throw new DataSourceProxyException("Could not find getParentLogger method");
        }

        try {
            return (Logger) this.proxyLogic.invoke(method, null);
        } catch (Throwable throwable) {
            if (throwable instanceof SQLFeatureNotSupportedException) {
                throw (SQLFeatureNotSupportedException) throwable;
            }
            throw new DataSourceProxyException("Failed to invoke method: getParentLogger", throwable);
        }
    }
}
