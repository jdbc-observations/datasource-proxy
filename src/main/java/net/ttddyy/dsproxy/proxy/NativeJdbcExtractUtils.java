package net.ttddyy.dsproxy.proxy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Retrieves the JDBC object that a datasource-proxy proxy wraps.
 *
 * <p>Every JDBC object created by datasource-proxy implements {@link ProxyJdbcObject}. The methods in this class call
 * {@link ProxyJdbcObject#getTarget()} repeatedly until they reach an object that is not a datasource-proxy proxy, so
 * nested proxies (a proxy of a proxy) are unwrapped too. An object that is not a datasource-proxy proxy is returned
 * unchanged.
 *
 * <p>Use it when code needs the object the proxy wraps, for example to call a driver-specific API or to pass the
 * object to a tool that checks its concrete class.
 *
 * <p>Difference from JDBC {@link java.sql.Wrapper#unwrap(Class)}: this class strips only datasource-proxy layers and
 * returns whatever they wrap, which may still be a connection-pool wrapper. {@code unwrap(Class)} is delegated to the
 * underlying driver or pool and returns an object of the requested type, going through their wrappers as well.
 *
 * <pre>
 * Connection connection = proxyDataSource.getConnection();
 * Connection original = NativeJdbcExtractUtils.getConnection(connection);
 * </pre>
 *
 * @author Tadaya Tsuyukubo
 */
public final class NativeJdbcExtractUtils {

    private NativeJdbcExtractUtils() {
    }

    /**
     * Returns the {@link Connection} wrapped by datasource-proxy proxies.
     *
     * @param connection a connection, proxied or not
     * @return the innermost non-proxy connection, or {@code connection} itself if it is not a proxy
     */
    public static Connection getConnection(Connection connection) {
        return getNativeJdbcObject(connection);
    }

    /**
     * Returns the {@link Statement} wrapped by datasource-proxy proxies.
     *
     * @param statement a statement, proxied or not
     * @return the innermost non-proxy statement, or {@code statement} itself if it is not a proxy
     */
    public static Statement getStatement(Statement statement) {
        return getNativeJdbcObject(statement);
    }

    /**
     * Returns the {@link PreparedStatement} wrapped by datasource-proxy proxies.
     *
     * @param preparedStatement a prepared statement, proxied or not
     * @return the innermost non-proxy prepared statement, or {@code preparedStatement} itself if it is not a proxy
     */
    public static PreparedStatement getPreparedStatement(PreparedStatement preparedStatement) {
        return getNativeJdbcObject(preparedStatement);
    }

    /**
     * Returns the {@link CallableStatement} wrapped by datasource-proxy proxies.
     *
     * @param callableStatement a callable statement, proxied or not
     * @return the innermost non-proxy callable statement, or {@code callableStatement} itself if it is not a proxy
     */
    public static CallableStatement getCallableStatement(CallableStatement callableStatement) {
        return getNativeJdbcObject(callableStatement);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getNativeJdbcObject(T obj) {
        T objToUse = obj;
        while (objToUse instanceof ProxyJdbcObject) {
            objToUse = (T) ((ProxyJdbcObject) objToUse).getTarget();
        }
        return objToUse;
    }

}
