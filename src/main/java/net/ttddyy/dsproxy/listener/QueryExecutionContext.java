package net.ttddyy.dsproxy.listener;

import org.jspecify.annotations.Nullable;

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

    @Nullable String getDataSourceName();

    @Nullable String getConnectionId();

    int getIsolationLevel();

    @Nullable Method getMethod();

    @Nullable Object @Nullable [] getMethodArgs();

    @Nullable Object getResult();

    @Nullable Throwable getThrowable();

    boolean isSuccess();

    /**
     * Elapsed time of the execution; zero before the query is executed.
     */
    Duration getElapsedTime();

    @Nullable StatementType getStatementType();

    boolean isBatch();

    int getBatchSize();

    @Nullable Statement getStatement();

    @Nullable ResultSet getGeneratedKeys();

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
    <T> @Nullable T getCustomValue(String key, Class<T> type);

    /**
     * Store key/value pair. Mainly used for passing values between before and after callbacks.
     *
     * @param key   key
     * @param value value
     */
    void addCustomValue(String key, Object value);

}
