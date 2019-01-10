package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.function.DSProxyBiConsumer;
import net.ttddyy.dsproxy.function.DSProxyFunction;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.proxy.ParameterSetOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Convert {@link QueryExecutionContext} to json {@code String}.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class QueryExecutionContextJsonFormatter extends AbstractFormatterSupport<QueryExecutionContext> {

    private DSProxyBiConsumer<QueryExecutionContext, StringBuilder> onDataSourceName = (queryContext, sb) -> {
        String name = queryContext.getDataSourceName();
        sb.append("\"name\":\"");
        sb.append(name == null ? "" : escapeSpecialCharacterForJson(name));
        sb.append("\"");
    };
    private DSProxyBiConsumer<QueryExecutionContext, StringBuilder> onConnection = (queryContext, sb) -> {
        sb.append("\"connection\":");
        sb.append(queryContext.getConnectionId());
    };
    private DSProxyBiConsumer<QueryExecutionContext, StringBuilder> onDuration = (queryContext, sb) -> {
        sb.append("\"time\":");
        sb.append(queryContext.getElapsedTime());
    };
    private DSProxyBiConsumer<QueryExecutionContext, StringBuilder> onSuccess = (queryContext, sb) -> {
        sb.append("\"success\":");
        sb.append(queryContext.isSuccess() ? "true" : "false");
    };
    private DSProxyBiConsumer<QueryExecutionContext, StringBuilder> onStatementType = (queryContext, sb) -> {
        sb.append("\"type\":\"");
        switch (queryContext.getStatementType()) {
            case STATEMENT:
                sb.append("Statement");
                break;
            case PREPARED:
                sb.append("Prepared");
                break;
            case CALLABLE:
                sb.append("Callable");
                break;
            default:
                sb.append("Unknown");
                break;
        }
        sb.append("\"");
    };
    private DSProxyBiConsumer<QueryExecutionContext, StringBuilder> onBatch = (queryContext, sb) -> {
        sb.append("\"batch\":");
        sb.append(queryContext.isBatch() ? "true" : "false");
    };
    private DSProxyBiConsumer<QueryExecutionContext, StringBuilder> onQuerySize = (queryContext, sb) -> {
        sb.append("\"querySize\":");
        sb.append(queryContext.getQueries().size());
    };
    private DSProxyBiConsumer<QueryExecutionContext, StringBuilder> onBatchSize = (queryContext, sb) -> {
        sb.append("\"batchSize\":");
        sb.append(queryContext.getBatchSize());
    };

    // Each query statement
    private DSProxyFunction<String, String> onQuery = AbstractFormatterSupport::escapeSpecialCharacterForJson;


    private DSProxyBiConsumer<QueryExecutionContext, StringBuilder> onQueries = (queryContext, sb) -> {
        sb.append("\"query\":[");
        for (QueryInfo queryInfo : queryContext.getQueries()) {
            sb.append("\"");
            sb.append(this.onQuery.apply(queryInfo.getQuery()));
            sb.append("\",");
        }
        chompIfEndWith(sb, ',');
        sb.append("]");
    };
    private DSProxyBiConsumer<QueryExecutionContext, StringBuilder> onParameters = (queryContext, sb) -> {
        boolean isPrepared = queryContext.getStatementType() == StatementType.PREPARED;

        sb.append("\"params\":[");

        for (QueryInfo query : queryContext.getQueries()) {
            for (ParameterSetOperations parameterSetOperations : query.getParameterSetOperations()) {
                SortedMap<String, String> paramMap = getParametersToDisplay(parameterSetOperations);

                // parameters per batch.
                //   for prepared: (val1,val2,...)
                //   for callable: (key1=val1,key2=val2,...)
                if (isPrepared) {
                    this.onPreparedParameters.accept(paramMap, sb);
                } else {
                    this.onCallableParameters.accept(paramMap, sb);
                }

            }
        }

        chompIfEndWith(sb, ',');
        sb.append("]");
    };

    /**
     * Write query parameters for PreparedStatement.
     *
     * default: ["foo","100"],
     */
    private DSProxyBiConsumer<SortedMap<String, String>, StringBuilder> onPreparedParameters = (paramMap, sb) -> {
        sb.append("[");

        for (String value : paramMap.values()) {
            if (value == null) {
                sb.append("null");
            } else {
                sb.append("\"");
                sb.append(escapeSpecialCharacterForJson(value));
                sb.append("\"");
            }
            sb.append(",");
        }

        chompIfEndWith(sb, ',');
        sb.append("],");
    };


    /**
     * Write parameters for single execution.
     *
     * <p>default: {"1"="foo","bar"="100"},
     */
    private DSProxyBiConsumer<SortedMap<String, String>, StringBuilder> onCallableParameters = (paramMap, sb) -> {
        sb.append("{");

        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            sb.append("\"");
            sb.append(escapeSpecialCharacterForJson(key));
            sb.append("\":");
            if (value == null) {
                sb.append("null");
            } else {
                sb.append("\"");
                sb.append(escapeSpecialCharacterForJson(value));
                sb.append("\"");
            }
            sb.append(",");

        }
        chompIfEndWith(sb, ',');
        sb.append("},");
    };

    private List<DSProxyBiConsumer<QueryExecutionContext, StringBuilder>> consumers = new ArrayList<>();


    public static QueryExecutionContextJsonFormatter showAll() {
        QueryExecutionContextJsonFormatter formatter = new QueryExecutionContextJsonFormatter();
        formatter.addConsumer(formatter.onDataSourceName);
        formatter.addConsumer(formatter.onConnection);
        formatter.addConsumer(formatter.onDuration);
        formatter.addConsumer(formatter.onSuccess);
        formatter.addConsumer(formatter.onStatementType);
        formatter.addConsumer(formatter.onBatch);
        formatter.addConsumer(formatter.onQuerySize);
        formatter.addConsumer(formatter.onBatchSize);
        formatter.addConsumer(formatter.onQueries);
        formatter.addConsumer(formatter.onParameters);
        return formatter;
    }

    public QueryExecutionContextJsonFormatter addConsumer(DSProxyBiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.consumers.add(consumer);
        return this;
    }

    public QueryExecutionContextJsonFormatter newLine() {
        this.consumers.add(this.newLine);
        return this;
    }

    public String format(QueryExecutionContext queryExecutionContext) {

        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (DSProxyBiConsumer<QueryExecutionContext, StringBuilder> consumer : consumers) {
            consumer.accept(queryExecutionContext, sb);

            if (consumer != this.newLine) {
                sb.append(this.delimiter);
            }
        }

        chompIfEndWith(sb, this.delimiter);

        sb.append("}");

        return sb.toString();

    }

    public QueryExecutionContextJsonFormatter showDataSourceName() {
        this.addConsumer(this.onDataSourceName);
        return this;
    }

    public QueryExecutionContextJsonFormatter showDataSourceName(DSProxyBiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onDataSourceName = consumer;
        return showDataSourceName();
    }

    public QueryExecutionContextJsonFormatter showDuration() {
        this.addConsumer(this.onDuration);
        return this;
    }

    public QueryExecutionContextJsonFormatter showDuration(DSProxyBiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onDuration = consumer;
        return showDuration();
    }


    public QueryExecutionContextJsonFormatter showSuccess() {
        this.addConsumer(this.onSuccess);
        return this;
    }

    public QueryExecutionContextJsonFormatter showSuccess(DSProxyBiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onSuccess = consumer;
        return showSuccess();
    }

    public QueryExecutionContextJsonFormatter showStatementType() {
        this.addConsumer(this.onStatementType);
        return this;
    }

    public QueryExecutionContextJsonFormatter showStatementType(DSProxyBiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onStatementType = consumer;
        return showStatementType();
    }


    public QueryExecutionContextJsonFormatter showBatch() {
        this.addConsumer(this.onBatch);
        return this;
    }

    public QueryExecutionContextJsonFormatter showBatch(DSProxyBiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onBatch = consumer;
        return showBatch();
    }

    public QueryExecutionContextJsonFormatter showQuerySize() {
        this.addConsumer(this.onQuerySize);
        return this;
    }

    public QueryExecutionContextJsonFormatter showQuerySize(DSProxyBiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onQuerySize = consumer;
        return showQuerySize();
    }

    public QueryExecutionContextJsonFormatter showBatchSize() {
        this.addConsumer(this.onBatchSize);
        return this;
    }

    public QueryExecutionContextJsonFormatter showBatchSize(DSProxyBiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onBatchSize = consumer;
        return showBatchSize();
    }


    public QueryExecutionContextJsonFormatter showQueries() {
        this.addConsumer(this.onQueries);
        return this;
    }

    public QueryExecutionContextJsonFormatter showQueries(DSProxyBiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onQueries = consumer;
        return showQueries();
    }

    public QueryExecutionContextJsonFormatter showParameters() {
        this.addConsumer(this.onParameters);
        return this;
    }

    public QueryExecutionContextJsonFormatter showParameters(DSProxyBiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onParameters = consumer;
        return showParameters();
    }

    public QueryExecutionContextJsonFormatter onQuery(DSProxyFunction<String, String> onQuery) {
        this.onQuery = onQuery;
        return this;
    }

    public QueryExecutionContextJsonFormatter onPreparedParameters(DSProxyBiConsumer<SortedMap<String, String>, StringBuilder> onPreparedParameters) {
        this.onPreparedParameters = onPreparedParameters;
        return this;
    }

    public QueryExecutionContextJsonFormatter onCallableParameters(DSProxyBiConsumer<SortedMap<String, String>, StringBuilder> onCallableParameters) {
        this.onCallableParameters = onCallableParameters;
        return this;
    }
}
