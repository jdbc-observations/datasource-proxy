package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Convert {@link QueryExecutionContext} to json {@code String}.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class QueryExecutionContextJsonFormatter extends AbstractFormatterSupport<QueryExecutionContext> implements Function<QueryExecutionContext, String> {

    protected ParameterValueConverter setNullParameterValueConverter = new SetNullParameterValueConverter();
    protected ParameterValueConverter registerOutParameterValueConverter = new RegisterOutParameterValueConverter();

    private BiConsumer<QueryExecutionContext, StringBuilder> onDataSourceName = (queryContext, sb) -> {
        String name = queryContext.getDataSourceName();
        sb.append("\"name\":\"");
        sb.append(name == null ? "" : escapeSpecialCharacterForJson(name));
        sb.append("\"");
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onConnection = (queryContext, sb) -> {
        sb.append("\"connection\":");
        sb.append(queryContext.getConnectionId());
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onDuration = (queryContext, sb) -> {
        sb.append("\"time\":");
        sb.append(queryContext.getElapsedTime());
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onSuccess = (queryContext, sb) -> {
        sb.append("\"success\":");
        sb.append(queryContext.isSuccess() ? "true" : "false");
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onStatementType = (queryContext, sb) -> {
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
    private BiConsumer<QueryExecutionContext, StringBuilder> onBatch = (queryContext, sb) -> {
        sb.append("\"batch\":");
        sb.append(queryContext.isBatch() ? "true" : "false");
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onQuerySize = (queryContext, sb) -> {
        sb.append("\"querySize\":");
        sb.append(queryContext.getQueries().size());
    };
    private BiConsumer<QueryExecutionContext, StringBuilder> onBatchSize = (queryContext, sb) -> {
        sb.append("\"batchSize\":");
        sb.append(queryContext.getBatchSize());
    };

    // Each query statement
    private Function<String, String> onQuery = AbstractFormatterSupport::escapeSpecialCharacterForJson;


    private BiConsumer<QueryExecutionContext, StringBuilder> onQueries = (queryContext, sb) -> {
        sb.append("\"query\":[");
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

        sb.append("\"params\":[");

        queryContext.getQueries().stream()
                .map(QueryInfo::getParameterSetOperations)
                .flatMap(Collection::stream)
                .forEach(parameterSetOperations -> {
                    SortedMap<String, String> paramMap = getParametersToDisplay(parameterSetOperations.getOperations());

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
     * default: ["foo","100"],
     */
    private BiConsumer<SortedMap<String, String>, StringBuilder> onPreparedParameters = (paramMap, sb) -> {
        sb.append("[");
        paramMap.values().forEach(value -> {
            if (value == null) {
                sb.append("null");
            } else {
                sb.append("\"");
                sb.append(escapeSpecialCharacterForJson(value));
                sb.append("\"");
            }
            sb.append(",");
        });

        chompIfEndWith(sb, ',');
        sb.append("],");
    };


    /**
     * Write parameters for single execution.
     *
     * <p>default: {"1"="foo","bar"="100"},
     */
    private BiConsumer<SortedMap<String, String>, StringBuilder> onCallableParameters = (paramMap, sb) -> {
        sb.append("{");
        paramMap.forEach((key, value) -> {
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
        });
        chompIfEndWith(sb, ',');
        sb.append("},");
    };


    protected SortedMap<String, String> getParametersToDisplay(List<ParameterSetOperation> params) {

        SortedMap<String, String> sortedMap = new TreeMap<>(new StringAsIntegerComparator());
        for (ParameterSetOperation param : params) {
            String key = getParameterKeyToDisplay(param);
            String value = getParameterValueToDisplay(param);
            sortedMap.put(key, value);
        }
        return sortedMap;
    }


    /**
     * Comparator considering string as integer.
     *
     * When it has null, put it as first element(smaller).
     * If string cannot be parsed to integer, it compared as string.
     */
    protected static class StringAsIntegerComparator implements Comparator<String> {
        @Override
        public int compare(String left, String right) {
            // make null first
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return -1; // right is greater
            }
            if (right == null) {
                return 1; // left is greater;
            }

            try {
                int leftInt = Integer.parseInt(left);
                int rightInt = Integer.parseInt(right);
                return Integer.compare(leftInt, rightInt);
            } catch (NumberFormatException e) {
                return left.compareTo(right);  // use String comparison
            }
        }

    }

    // TODO: cleanup
    public String getParameterKeyToDisplay(ParameterSetOperation param) {
        Object key = param.getArgs()[0];  // either int(parameterIndex) or string(parameterName)
        return key instanceof String ? (String) key : key.toString();
    }

    // TODO: cleanup
    protected String getParameterValueToDisplay(ParameterSetOperation param) {

        String value;
        if (ParameterSetOperation.isSetNullParameterOperation(param)) {
            // for setNull
            value = getDisplayValueForSetNull(param);
        } else if (ParameterSetOperation.isRegisterOutParameterOperation(param)) {
            // for registerOutParameter
            value = getDisplayValueForRegisterOutParameter(param);
        } else {
            value = getDisplayValue(param);
        }
        return value;
    }

    private List<BiConsumer<QueryExecutionContext, StringBuilder>> consumers = new ArrayList<>();


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

    public QueryExecutionContextJsonFormatter addConsumer(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
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
        this.consumers.forEach(consumer -> {

            consumer.accept(queryExecutionContext, sb);

            if (consumer != this.newLine) {
                sb.append(this.delimiter);
            }
        });

        chompIfEndWith(sb, this.delimiter);

        sb.append("}");

        return sb.toString();

    }

    @Override
    public String apply(QueryExecutionContext queryExecutionContext) {
        return format(queryExecutionContext);
    }

    public String getDisplayValueForSetNull(ParameterSetOperation param) {
        return this.setNullParameterValueConverter.getValue(param);
    }

    public String getDisplayValueForRegisterOutParameter(ParameterSetOperation param) {
        return this.registerOutParameterValueConverter.getValue(param);
    }

    public String getDisplayValue(ParameterSetOperation param) {
        Object value = param.getArgs()[1];
        return value == null ? null : value.toString();
    }

    public QueryExecutionContextJsonFormatter showDataSourceName() {
        this.addConsumer(this.onDataSourceName);
        return this;
    }

    public QueryExecutionContextJsonFormatter showDataSourceName(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onDataSourceName = consumer;
        return showDataSourceName();
    }

    public QueryExecutionContextJsonFormatter showDuration() {
        this.addConsumer(this.onDuration);
        return this;
    }

    public QueryExecutionContextJsonFormatter showDuration(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onDuration = consumer;
        return showDuration();
    }


    public QueryExecutionContextJsonFormatter showSuccess() {
        this.addConsumer(this.onSuccess);
        return this;
    }

    public QueryExecutionContextJsonFormatter showSuccess(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onSuccess = consumer;
        return showSuccess();
    }

    public QueryExecutionContextJsonFormatter showStatementType() {
        this.addConsumer(this.onStatementType);
        return this;
    }

    public QueryExecutionContextJsonFormatter showStatementType(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onStatementType = consumer;
        return showStatementType();
    }


    public QueryExecutionContextJsonFormatter showBatch() {
        this.addConsumer(this.onBatch);
        return this;
    }

    public QueryExecutionContextJsonFormatter showBatch(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onBatch = consumer;
        return showBatch();
    }

    public QueryExecutionContextJsonFormatter showQuerySize() {
        this.addConsumer(this.onQuerySize);
        return this;
    }

    public QueryExecutionContextJsonFormatter showQuerySize(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onQuerySize = consumer;
        return showQuerySize();
    }

    public QueryExecutionContextJsonFormatter showBatchSize() {
        this.addConsumer(this.onBatchSize);
        return this;
    }

    public QueryExecutionContextJsonFormatter showBatchSize(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onBatchSize = consumer;
        return showBatchSize();
    }


    public QueryExecutionContextJsonFormatter showQueries() {
        this.addConsumer(this.onQueries);
        return this;
    }

    public QueryExecutionContextJsonFormatter showQueries(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onQueries = consumer;
        return showQueries();
    }

    public QueryExecutionContextJsonFormatter showParameters() {
        this.addConsumer(this.onParameters);
        return this;
    }

    public QueryExecutionContextJsonFormatter showParameters(BiConsumer<QueryExecutionContext, StringBuilder> consumer) {
        this.onParameters = consumer;
        return showParameters();
    }

    public QueryExecutionContextJsonFormatter onQuery(Function<String, String> onQuery) {
        this.onQuery = onQuery;
        return this;
    }

    public QueryExecutionContextJsonFormatter onPreparedParameters(BiConsumer<SortedMap<String, String>, StringBuilder> onPreparedParameters) {
        this.onPreparedParameters = onPreparedParameters;
        return this;
    }

    public QueryExecutionContextJsonFormatter onCallableParameters(BiConsumer<SortedMap<String, String>, StringBuilder> onCallableParameters) {
        this.onCallableParameters = onCallableParameters;
        return this;
    }
}
