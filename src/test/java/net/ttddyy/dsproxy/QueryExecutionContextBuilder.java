package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;

import java.lang.reflect.Method;
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
    private long elapsedTime;
    private Throwable throwable;
    private StatementType statementType;
    private boolean success;
    private boolean batch;
    private int batchSize;
    private String connectionId;
    private List<QueryInfo> queries = new ArrayList<>();
    private long threadId;
    private String threadName;

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

    public QueryExecutionContextBuilder elapsedTime(long elapsedTime) {
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

    public QueryExecutionContextBuilder queries(List<QueryInfo> queries) {
        this.queries = queries;
        return this;
    }

    /**
     * @since 2.0
     */
    public QueryExecutionContextBuilder threadId(long threadId) {
        this.threadId = threadId;
        return this;
    }

    /**
     * @since 2.0
     */
    public QueryExecutionContextBuilder threadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    public QueryExecutionContext build() {
        QueryExecutionContext queryExecutionContext = new QueryExecutionContext();
        queryExecutionContext.setDataSourceName(dataSourceName);
        queryExecutionContext.setMethod(method);
        queryExecutionContext.setMethodArgs(methodArgs);
        queryExecutionContext.setResult(result);
        queryExecutionContext.setElapsedTime(elapsedTime);
        queryExecutionContext.setThrowable(throwable);
        queryExecutionContext.setStatementType(statementType);
        queryExecutionContext.setSuccess(success);
        queryExecutionContext.setBatch(batch);
        queryExecutionContext.setBatchSize(batchSize);
        queryExecutionContext.setConnectionId(this.connectionId);
        queryExecutionContext.setQueries(this.queries);
        queryExecutionContext.setThreadId(this.threadId);
        queryExecutionContext.setThreadName(this.threadName);
        return queryExecutionContext;
    }
}
