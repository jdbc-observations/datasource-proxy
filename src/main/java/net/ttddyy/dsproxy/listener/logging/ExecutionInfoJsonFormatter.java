package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
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
 * Convert {@link ExecutionInfo} to json {@code String}.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class ExecutionInfoJsonFormatter extends AbstractFormatterSupport implements Function<ExecutionInfo, String> {

    private static final String DEFAULT_DELIMITER = ", ";

    protected ParameterValueConverter setNullParameterValueConverter = new SetNullParameterValueConverter();
    protected ParameterValueConverter registerOutParameterValueConverter = new RegisterOutParameterValueConverter();

    private BiConsumer<ExecutionInfo, StringBuilder> onDataSourceName = (execInfo, sb) -> {
        String name = execInfo.getDataSourceName();
        sb.append("\"name\":\"");
        sb.append(name == null ? "" : escapeSpecialCharacterForJson(name));
        sb.append("\"");
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onConnection = (execInfo, sb) -> {
        sb.append("\"connection\":");
        sb.append(execInfo.getConnectionId());
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onDuration = (execInfo, sb) -> {
        sb.append("\"time\":");
        sb.append(execInfo.getElapsedTime());
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onSuccess = (execInfo, sb) -> {
        sb.append("\"success\":");
        sb.append(execInfo.isSuccess() ? "true" : "false");
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onStatementType = (execInfo, sb) -> {
        sb.append("\"type\":\"");
        switch (execInfo.getStatementType()) {
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
    private BiConsumer<ExecutionInfo, StringBuilder> onBatch = (execInfo, sb) -> {
        sb.append("\"batch\":");
        sb.append(execInfo.isBatch() ? "true" : "false");
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onQuerySize = (execInfo, sb) -> {
        sb.append("\"querySize\":");
        sb.append(execInfo.getQueries().size());
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onBatchSize = (execInfo, sb) -> {
        sb.append("\"batchSize\":");
        sb.append(execInfo.getBatchSize());
    };

    // Each query statement
    private Function<String, String> onQuery = AbstractFormatterSupport::escapeSpecialCharacterForJson;


    private BiConsumer<ExecutionInfo, StringBuilder> onQueries = (execInfo, sb) -> {
        sb.append("\"query\":[");
        execInfo.getQueries().forEach(queryInfo -> {
            sb.append("\"");
            sb.append(this.onQuery.apply(queryInfo.getQuery()));
            sb.append("\",");
        });
        chompIfEndWith(sb, ',');
        sb.append("]");
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onParameters = (execInfo, sb) -> {
        boolean isPrepared = execInfo.getStatementType() == StatementType.PREPARED;

        sb.append("\"params\":[");

        execInfo.getQueries().stream()
                .map(QueryInfo::getParametersList)
                .flatMap(Collection::stream)
                .forEach(parameters -> {
                    SortedMap<String, String> paramMap = getParametersToDisplay(parameters);

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

    private BiConsumer<ExecutionInfo, StringBuilder> newLine = (executionInfo, sb) -> {
        sb.append(System.lineSeparator());
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

    private String delimiter = DEFAULT_DELIMITER;

    private List<BiConsumer<ExecutionInfo, StringBuilder>> consumers = new ArrayList<>();


    public static ExecutionInfoJsonFormatter showAll() {
        ExecutionInfoJsonFormatter formatter = new ExecutionInfoJsonFormatter();
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

    public ExecutionInfoJsonFormatter addConsumer(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.consumers.add(consumer);
        return this;
    }

    public ExecutionInfoJsonFormatter newLine() {
        this.consumers.add(this.newLine);
        return this;
    }

    public String format(ExecutionInfo executionInfo) {

        StringBuilder sb = new StringBuilder();

        sb.append("{");
        this.consumers.forEach(consumer -> {

            consumer.accept(executionInfo, sb);

            if (consumer != this.newLine) {
                sb.append(this.delimiter);
            }
        });

        chompIfEndWith(sb, this.delimiter);

        sb.append("}");

        return sb.toString();

    }

    @Override
    public String apply(ExecutionInfo executionInfo) {
        return format(executionInfo);
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

    public ExecutionInfoJsonFormatter showDataSourceName() {
        this.addConsumer(this.onDataSourceName);
        return this;
    }

    public ExecutionInfoJsonFormatter showDataSourceName(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onDataSourceName = consumer;
        return showDataSourceName();
    }

    public ExecutionInfoJsonFormatter showDuration() {
        this.addConsumer(this.onDuration);
        return this;
    }

    public ExecutionInfoJsonFormatter showDuration(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onDuration = consumer;
        return showDuration();
    }


    public ExecutionInfoJsonFormatter showSuccess() {
        this.addConsumer(this.onSuccess);
        return this;
    }

    public ExecutionInfoJsonFormatter showSuccess(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onSuccess = consumer;
        return showSuccess();
    }

    public ExecutionInfoJsonFormatter showStatementType() {
        this.addConsumer(this.onStatementType);
        return this;
    }

    public ExecutionInfoJsonFormatter showStatementType(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onStatementType = consumer;
        return showStatementType();
    }


    public ExecutionInfoJsonFormatter showBatch() {
        this.addConsumer(this.onBatch);
        return this;
    }

    public ExecutionInfoJsonFormatter showBatch(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onBatch = consumer;
        return showBatch();
    }

    public ExecutionInfoJsonFormatter showQuerySize() {
        this.addConsumer(this.onQuerySize);
        return this;
    }

    public ExecutionInfoJsonFormatter showQuerySize(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onQuerySize = consumer;
        return showQuerySize();
    }

    public ExecutionInfoJsonFormatter showBatchSize() {
        this.addConsumer(this.onBatchSize);
        return this;
    }

    public ExecutionInfoJsonFormatter showBatchSize(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onBatchSize = consumer;
        return showBatchSize();
    }


    public ExecutionInfoJsonFormatter showQueries() {
        this.addConsumer(this.onQueries);
        return this;
    }

    public ExecutionInfoJsonFormatter showQueries(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onQueries = consumer;
        return showQueries();
    }

    public ExecutionInfoJsonFormatter showParameters() {
        this.addConsumer(this.onParameters);
        return this;
    }

    public ExecutionInfoJsonFormatter showParameters(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onParameters = consumer;
        return showParameters();
    }

    public ExecutionInfoJsonFormatter onQuery(Function<String, String> onQuery) {
        this.onQuery = onQuery;
        return this;
    }

    public ExecutionInfoJsonFormatter onPreparedParameters(BiConsumer<SortedMap<String, String>, StringBuilder> onPreparedParameters) {
        this.onPreparedParameters = onPreparedParameters;
        return this;
    }

    public ExecutionInfoJsonFormatter onCallableParameters(BiConsumer<SortedMap<String, String>, StringBuilder> onCallableParameters) {
        this.onCallableParameters = onCallableParameters;
        return this;
    }
}
