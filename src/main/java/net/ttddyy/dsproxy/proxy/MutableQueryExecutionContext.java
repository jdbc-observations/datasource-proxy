package net.ttddyy.dsproxy.proxy;

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

    private String dataSourceName;
    private String connectionId;
    private int isolationLevel;
    private Method method;
    private Object[] methodArgs;
    private Object result;
    private Throwable throwable;
    private boolean success;
    private Duration elapsedTime = Duration.ZERO;
    private StatementType statementType;
    private boolean batch;
    private int batchSize;
    private Statement statement;
    private ResultSet generatedKeys;
    private List<QueryInfo> queries = List.of();
    private final Map<String, Object> customValues = new HashMap<>();

    // shortcut: temporary 1.x listener bridge, removed in Task 7 (Phase 2)
    StopwatchFactory stopwatchFactory;  // lets LegacyListenerBridge report elapsed time in the 1.x unit

    @Override
    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public String getConnectionId() {
        return this.connectionId;
    }

    public void setConnectionId(String connectionId) {
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
    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public Object[] getMethodArgs() {
        return this.methodArgs;
    }

    public void setMethodArgs(Object[] methodArgs) {
        this.methodArgs = methodArgs;
    }

    @Override
    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }

    public void setThrowable(Throwable throwable) {
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
    public StatementType getStatementType() {
        return this.statementType;
    }

    public void setStatementType(StatementType statementType) {
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
    public Statement getStatement() {
        return this.statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public ResultSet getGeneratedKeys() {
        return this.generatedKeys;
    }

    public void setGeneratedKeys(ResultSet generatedKeys) {
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
    public <T> T getCustomValue(String key, Class<T> type) {
        return type.cast(this.customValues.get(key));
    }

    @Override
    public void addCustomValue(String key, Object value) {
        this.customValues.put(key, value);
    }

}
