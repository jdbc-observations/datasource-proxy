package net.ttddyy.dsproxy.proxy;

import org.jspecify.annotations.Nullable;

import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mutable {@link QueryExecutionContext} used by the proxy logic to populate values.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class MutableQueryExecutionContext implements QueryExecutionContext {

    private @Nullable String dataSourceName;
    private @Nullable String connectionId;
    private int isolationLevel;
    private @Nullable Method method;
    private @Nullable Object @Nullable [] methodArgs;
    private @Nullable Object result;
    private @Nullable Throwable throwable;
    private boolean success;
    private Duration elapsedTime = Duration.ZERO;
    private @Nullable StatementType statementType;
    private boolean batch;
    private int batchSize;
    private @Nullable Statement statement;
    private @Nullable ResultSet generatedKeys;
    private List<QueryInfo> queries = List.of();
    private final Map<String, Object> customValues = new HashMap<>();

    @Override
    public @Nullable String getDataSourceName() {
        return this.dataSourceName;
    }

    public void setDataSourceName(@Nullable String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public @Nullable String getConnectionId() {
        return this.connectionId;
    }

    public void setConnectionId(@Nullable String connectionId) {
        this.connectionId = connectionId;
    }

    @Override
    public int getIsolationLevel() {
        return this.isolationLevel;
    }

    public void setIsolationLevel(int isolationLevel) {
        this.isolationLevel = isolationLevel;
    }

    @Override
    public @Nullable Method getMethod() {
        return this.method;
    }

    public void setMethod(@Nullable Method method) {
        this.method = method;
    }

    @Override
    public @Nullable Object @Nullable [] getMethodArgs() {
        return this.methodArgs;
    }

    public void setMethodArgs(@Nullable Object @Nullable [] methodArgs) {
        this.methodArgs = methodArgs;
    }

    @Override
    public @Nullable Object getResult() {
        return this.result;
    }

    public void setResult(@Nullable Object result) {
        this.result = result;
    }

    @Override
    public @Nullable Throwable getThrowable() {
        return this.throwable;
    }

    public void setThrowable(@Nullable Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public Duration getElapsedTime() {
        return this.elapsedTime;
    }

    public void setElapsedTime(Duration elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @Override
    public @Nullable StatementType getStatementType() {
        return this.statementType;
    }

    public void setStatementType(@Nullable StatementType statementType) {
        this.statementType = statementType;
    }

    @Override
    public boolean isBatch() {
        return this.batch;
    }

    public void setBatch(boolean batch) {
        this.batch = batch;
    }

    @Override
    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public @Nullable Statement getStatement() {
        return this.statement;
    }

    public void setStatement(@Nullable Statement statement) {
        this.statement = statement;
    }

    @Override
    public @Nullable ResultSet getGeneratedKeys() {
        return this.generatedKeys;
    }

    public void setGeneratedKeys(@Nullable ResultSet generatedKeys) {
        this.generatedKeys = generatedKeys;
    }

    @Override
    public List<QueryInfo> getQueries() {
        return this.queries;
    }

    public void setQueries(List<QueryInfo> queries) {
        this.queries = queries;
    }

    @Override
    public <T> @Nullable T getCustomValue(String key, Class<T> type) {
        return type.cast(this.customValues.get(key));
    }

    @Override
    public void addCustomValue(String key, Object value) {
        this.customValues.put(key, value);
    }

}
