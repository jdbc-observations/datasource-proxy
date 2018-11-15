package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import net.ttddyy.dsproxy.proxy.ParameterSetOperations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Convert {@link QueryExecutionContext} to {@code String}.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class QueryExecutionContextFormatter extends AbstractFormatterSupport<QueryExecutionContext> implements Function<QueryExecutionContext, String> {

    private BiConsumer<QueryExecutionContext, StringBuilder> onDataSourceName = (queryContext, sb) -> {
        String name = queryContext.getDataSourceName();
        sb.append("Name:");
        sb.append(name == null ? "" : name);
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onConnection = (queryContext, sb) -> {
        sb.append("Connection:");
        sb.append(queryContext.getConnectionId());
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onDuration = (queryContext, sb) -> {
        sb.append("Time:");
        sb.append(queryContext.getElapsedTime());
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onThread = (queryContext, sb) -> {
        sb.append("Thread:");
        sb.append(queryContext.getThreadName());
        sb.append("(");
        sb.append(queryContext.getThreadId());
        sb.append(")");
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onSuccess = (queryContext, sb) -> {
        sb.append("Success:");
        sb.append(queryContext.isSuccess() ? "True" : "False");
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onStatementType = (queryContext, sb) -> {
        sb.append("Type:");
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
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onBatch = (queryContext, sb) -> {
        sb.append("Batch:");
        sb.append(queryContext.isBatch() ? "True" : "False");
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onQuerySize = (queryContext, sb) -> {
        sb.append("QuerySize:");
        sb.append(queryContext.getQueries().size());
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onBatchSize = (queryContext, sb) -> {
        sb.append("BatchSize:");
        sb.append(queryContext.getBatchSize());
    };

    // Each query statement
    private Function<String, String> onQuery = Function.identity();


    private BiConsumer<QueryExecutionContext, StringBuilder> onQueries = (queryContext, sb) -> {
        sb.append("Query:[");
        queryContext.getQueries().forEach(queryInfo -> {
            sb.append("\"");
            sb.append(this.onQuery.apply(queryInfo.getQuery()));
            sb.append("\",");
        });
        chompIfEndWith(sb, ',');

        sb.append("]");
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onParameters = (queryContext, sb) -> {
        boolean isPrepared = queryContext.getStatementType() == StatementType.PREPARED;

        sb.append("Params:[");

        queryContext.getQueries().stream()
                .map(QueryInfo::getParameterSetOperations)
                .flatMap(Collection::stream)
                .forEach(parameterSetOperations -> {
                    SortedMap<String, String> paramMap = getParametersToDisplay(parameterSetOperations);

                    // parameters per batch.
                    //   for prepared: (val1,val2,...)
                    //   for callable: (key1=val1,key2=val2,...)
                    if (isPrepared) {
                        this.onPreparedParameters.accept(paramMap, sb);
                    } else {
                        this.onCallableParameters.accept(paramMap, sb);
                    }

                });

        chompIfEndWith(sb, ',');
        sb.append("]");
    };

    /**
     * Write query parameters for PreparedStatement.
     *
     * default: Params:[(foo,100),(bar,101)],
     */
    private BiConsumer<SortedMap<String, String>, StringBuilder> onPreparedParameters = (paramMap, sb) -> {
        sb.append("(");
        sb.append(String.join(",", paramMap.values()));
        sb.append("),");
    };


    /**
     * Write parameters for single execution.
     *
     * <p>default: (1=foo,bar=100),
     */
    private BiConsumer<SortedMap<String, String>, StringBuilder> onCallableParameters = (paramMap, sb) -> {
        sb.append("(");
        paramMap.forEach((key, value) -> {
            sb.append(key);
            sb.append("=");
            sb.append(value);
            sb.append(",");
        });
        chompIfEndWith(sb, ',');
        sb.append("),");
    };


    private List<BiConsumer<QueryExecutionContext, StringBuilder>> consumers = new ArrayList<>();


    public static QueryExecutionContextFormatter showAll() {
        QueryExecutionContextFormatter formatter = new QueryExecutionContextFormatter();
        formatter.addConsumer(formatter.onDataSourceName);
        formatter.addConsumer(formatter.onConnection);
        formatter.addConsumer(formatter.onDuration);
        formatter.addConsumer(formatter.onThread);
        formatter.addConsumer(formatter.onSuccess);
        formatter.addConsumer(formatter.onStatementType);
        formatter.addConsumer(formatter.onBatch);
        formatter.addConsumer(formatter.onQuerySize);
        formatter.addConsumer(formatter.onBatchSize);
        formatter.addConsumer(formatter.onQueries);
        formatter.addConsumer(formatter.onParameters);
        return formatter;
    }

    public QueryExecutionContextFormatter addConsumer(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.consumers.add(consumer);
        return this;
    }

    public QueryExecutionContextFormatter newLine() {
        this.consumers.add(this.newLine);
        return this;
    }

    public String format(QueryExecutionContext queryExecutionContext) {

        StringBuilder sb = new StringBuilder();

        this.consumers.forEach(consumer -> {

            // if it is newLine, remove previously added delimiter
            if (consumer == this.newLine) {
                chompIfEndWith(sb, this.delimiter);
            }

            consumer.accept(queryExecutionContext, sb);

            if (consumer != this.newLine) {
                sb.append(this.delimiter);
            }
        });

        chompIfEndWith(sb, this.delimiter);

        return sb.toString();

    }

    @Override
    public String apply(QueryExecutionContext queryExecutionContext) {
        return format(queryExecutionContext);
    }

    public QueryExecutionContextFormatter showDataSourceName() {
        this.addConsumer(this.onDataSourceName);
        return this;
    }

    public QueryExecutionContextFormatter showDataSourceName(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onDataSourceName = consumer;
        return showDataSourceName();
    }

    public QueryExecutionContextFormatter showDuration() {
        this.addConsumer(this.onDuration);
        return this;
    }

    public QueryExecutionContextFormatter showDuration(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onDuration = consumer;
        return showDuration();
    }

    public QueryExecutionContextFormatter showThread() {
        this.addConsumer(this.onThread);
        return this;
    }

    public QueryExecutionContextFormatter showThread(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onThread = consumer;
        return showThread();
    }


    public QueryExecutionContextFormatter showSuccess() {
        this.addConsumer(this.onSuccess);
        return this;
    }

    public QueryExecutionContextFormatter showSuccess(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onSuccess = consumer;
        return showSuccess();
    }

    public QueryExecutionContextFormatter showStatementType() {
        this.addConsumer(this.onStatementType);
        return this;
    }

    public QueryExecutionContextFormatter showStatementType(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onStatementType = consumer;
        return showStatementType();
    }


    public QueryExecutionContextFormatter showBatch() {
        this.addConsumer(this.onBatch);
        return this;
    }

    public QueryExecutionContextFormatter showBatch(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onBatch = consumer;
        return showBatch();
    }

    public QueryExecutionContextFormatter showQuerySize() {
        this.addConsumer(this.onQuerySize);
        return this;
    }

    public QueryExecutionContextFormatter showQuerySize(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onQuerySize = consumer;
        return showQuerySize();
    }

    public QueryExecutionContextFormatter showBatchSize() {
        this.addConsumer(this.onBatchSize);
        return this;
    }

    public QueryExecutionContextFormatter showBatchSize(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onBatchSize = consumer;
        return showBatchSize();
    }


    public QueryExecutionContextFormatter showQueries() {
        this.addConsumer(this.onQueries);
        return this;
    }

    public QueryExecutionContextFormatter showQueries(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onQueries = consumer;
        return showQueries();
    }

    public QueryExecutionContextFormatter showParameters() {
        this.addConsumer(this.onParameters);
        return this;
    }

    public QueryExecutionContextFormatter showParameters(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onParameters = consumer;
        return showParameters();
    }

    public QueryExecutionContextFormatter onQuery(Function<String, String> onQuery) {
        this.onQuery = onQuery;
        return this;
    }

    public QueryExecutionContextFormatter onPreparedParameters(BiConsumer<SortedMap<String, String>, StringBuilder> onPreparedParameters) {
        this.onPreparedParameters = onPreparedParameters;
        return this;
    }

    public QueryExecutionContextFormatter onCallableParameters(BiConsumer<SortedMap<String, String>, StringBuilder> onCallableParameters) {
        this.onCallableParameters = onCallableParameters;
        return this;
    }
}
