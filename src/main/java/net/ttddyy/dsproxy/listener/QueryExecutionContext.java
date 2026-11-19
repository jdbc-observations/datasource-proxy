package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.List;

/**
 * Read-only view of a query execution, passed to {@link ProxyDataSourceListener}.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public interface QueryExecutionContext {

    String getDataSourceName();

    String getConnectionId();

    int getIsolationLevel();

    Method getMethod();

    Object[] getMethodArgs();

    Object getResult();

    Throwable getThrowable();

    boolean isSuccess();

    /**
     * Elapsed time of the execution; zero before the query is executed.
     */
    Duration getElapsedTime();

    StatementType getStatementType();

    boolean isBatch();

    int getBatchSize();

    Statement getStatement();

    ResultSet getGeneratedKeys();

    /**
     * Queries (with parameters) being executed.
     */
    List<QueryInfo> getQueries();

    /**
     * Retrieve a value stored by {@link #addCustomValue(String, Object)}.
     *
     * @param key  key
     * @param type expected value type
     * @return stored value, or {@code null}
     */
    <T> T getCustomValue(String key, Class<T> type);

    /**
     * Store key/value pair. Mainly used for passing values between before and after callbacks.
     *
     * @param key   key
     * @param value value
     */
    void addCustomValue(String key, Object value);

}
