package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.proxy.MutableQueryExecutionContext;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class QueryExecutionContextBuilder {
    private String dataSourceName;
    private Method method;
    private Object[] methodArgs;
    private Object result;
    private Duration elapsedTime = Duration.ZERO;
    private Throwable throwable;
    private StatementType statementType;
    private boolean success;
    private boolean batch;
    private int batchSize;
    private String connectionId;
    private int isolationLevel;
    private List<QueryInfo> queries = new ArrayList<>();

    /**
     * Set the queries on the given context and return it.
     */
    public static MutableQueryExecutionContext withQueries(MutableQueryExecutionContext context, List<QueryInfo> queries) {
        context.setQueries(queries);
        return context;
    }

    public static QueryExecutionContextBuilder create() {
        return new QueryExecutionContextBuilder();
    }

    public QueryExecutionContextBuilder dataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
        return this;
    }

    public QueryExecutionContextBuilder method(Method method) {
        this.method = method;
        return this;
    }

    public QueryExecutionContextBuilder methodArgs(Object[] methodArgs) {
        this.methodArgs = methodArgs;
        return this;
    }

    public QueryExecutionContextBuilder result(Object result) {
        this.result = result;
        return this;
    }

    public QueryExecutionContextBuilder elapsedTime(Duration elapsedTime) {
        this.elapsedTime = elapsedTime;
        return this;
    }

    public QueryExecutionContextBuilder throwable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    public QueryExecutionContextBuilder statementType(StatementType statementType) {
        this.statementType = statementType;
        return this;
    }

    public QueryExecutionContextBuilder success(boolean success) {
        this.success = success;
        return this;
    }

    public QueryExecutionContextBuilder batch(boolean batch) {
        this.batch = batch;
        return this;
    }

    public QueryExecutionContextBuilder batchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public QueryExecutionContextBuilder connectionId(String connectionId) {
        this.connectionId = connectionId;
        return this;
    }

    public QueryExecutionContextBuilder isolationLevel(int isolationLevel) {
        this.isolationLevel = isolationLevel;
        return this;
    }

    public QueryExecutionContextBuilder queries(List<QueryInfo> queries) {
        this.queries = queries;
        return this;
    }

    public MutableQueryExecutionContext build() {
        MutableQueryExecutionContext executionInfo = new MutableQueryExecutionContext();
        executionInfo.setDataSourceName(dataSourceName);
        executionInfo.setMethod(method);
        executionInfo.setMethodArgs(methodArgs);
        executionInfo.setResult(result);
        executionInfo.setElapsedTime(elapsedTime);
        executionInfo.setThrowable(throwable);
        executionInfo.setStatementType(statementType);
        executionInfo.setSuccess(success);
        executionInfo.setBatch(batch);
        executionInfo.setBatchSize(batchSize);
        executionInfo.setConnectionId(this.connectionId);
        executionInfo.setIsolationLevel(isolationLevel);
        executionInfo.setQueries(queries);
        return executionInfo;
    }
}
