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
 * Convert {@link ExecutionInfo} to {@code String}.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class ExecutionInfoFormatter extends AbstractFormatterSupport<ExecutionInfo> implements Function<ExecutionInfo, String> {

    protected ParameterValueConverter setNullParameterValueConverter = new SetNullParameterValueConverter();
    protected ParameterValueConverter registerOutParameterValueConverter = new RegisterOutParameterValueConverter();

    private BiConsumer<ExecutionInfo, StringBuilder> onDataSourceName = (execInfo, sb) -> {
        String name = execInfo.getDataSourceName();
        sb.append("Name:");
        sb.append(name == null ? "" : name);
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onConnection = (execInfo, sb) -> {
        sb.append("Connection:");
        sb.append(execInfo.getConnectionId());
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onDuration = (execInfo, sb) -> {
        sb.append("Time:");
        sb.append(execInfo.getElapsedTime());
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onSuccess = (execInfo, sb) -> {
        sb.append("Success:");
        sb.append(execInfo.isSuccess() ? "True" : "False");
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onStatementType = (execInfo, sb) -> {
        sb.append("Type:");
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
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onBatch = (execInfo, sb) -> {
        sb.append("Batch:");
        sb.append(execInfo.isBatch() ? "True" : "False");
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onQuerySize = (execInfo, sb) -> {
        sb.append("QuerySize:");
        sb.append(execInfo.getQueries().size());
    };
    private BiConsumer<ExecutionInfo, StringBuilder> onBatchSize = (execInfo, sb) -> {
        sb.append("BatchSize:");
        sb.append(execInfo.getBatchSize());
    };

    // Each query statement
    private Function<String, String> onQuery = Function.identity();


    private BiConsumer<ExecutionInfo, StringBuilder> onQueries = (execInfo, sb) -> {
        sb.append("Query:[");
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

        sb.append("Params:[");

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

    private List<BiConsumer<ExecutionInfo, StringBuilder>> consumers = new ArrayList<>();


    public static ExecutionInfoFormatter showAll() {
        ExecutionInfoFormatter formatter = new ExecutionInfoFormatter();
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

    public ExecutionInfoFormatter addConsumer(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.consumers.add(consumer);
        return this;
    }

    public ExecutionInfoFormatter newLine() {
        this.consumers.add(this.newLine);
        return this;
    }

    public String format(ExecutionInfo executionInfo) {

        StringBuilder sb = new StringBuilder();

        this.consumers.forEach(consumer -> {

            // if it is newLine, remove previously added delimiter
            if (consumer == this.newLine) {
                chompIfEndWith(sb, this.delimiter);
            }

            consumer.accept(executionInfo, sb);

            if (consumer != this.newLine) {
                sb.append(this.delimiter);
            }
        });

        chompIfEndWith(sb, this.delimiter);

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

    public ExecutionInfoFormatter showDataSourceName() {
        this.addConsumer(this.onDataSourceName);
        return this;
    }

    public ExecutionInfoFormatter showDataSourceName(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onDataSourceName = consumer;
        return showDataSourceName();
    }

    public ExecutionInfoFormatter showDuration() {
        this.addConsumer(this.onDuration);
        return this;
    }

    public ExecutionInfoFormatter showDuration(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onDuration = consumer;
        return showDuration();
    }


    public ExecutionInfoFormatter showSuccess() {
        this.addConsumer(this.onSuccess);
        return this;
    }

    public ExecutionInfoFormatter showSuccess(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onSuccess = consumer;
        return showSuccess();
    }

    public ExecutionInfoFormatter showStatementType() {
        this.addConsumer(this.onStatementType);
        return this;
    }

    public ExecutionInfoFormatter showStatementType(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onStatementType = consumer;
        return showStatementType();
    }


    public ExecutionInfoFormatter showBatch() {
        this.addConsumer(this.onBatch);
        return this;
    }

    public ExecutionInfoFormatter showBatch(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onBatch = consumer;
        return showBatch();
    }

    public ExecutionInfoFormatter showQuerySize() {
        this.addConsumer(this.onQuerySize);
        return this;
    }

    public ExecutionInfoFormatter showQuerySize(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onQuerySize = consumer;
        return showQuerySize();
    }

    public ExecutionInfoFormatter showBatchSize() {
        this.addConsumer(this.onBatchSize);
        return this;
    }

    public ExecutionInfoFormatter showBatchSize(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onBatchSize = consumer;
        return showBatchSize();
    }


    public ExecutionInfoFormatter showQueries() {
        this.addConsumer(this.onQueries);
        return this;
    }

    public ExecutionInfoFormatter showQueries(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onQueries = consumer;
        return showQueries();
    }

    public ExecutionInfoFormatter showParameters() {
        this.addConsumer(this.onParameters);
        return this;
    }

    public ExecutionInfoFormatter showParameters(BiConsumer<ExecutionInfo, StringBuilder> consumer) {
        this.onParameters = consumer;
        return showParameters();
    }

    public ExecutionInfoFormatter onQuery(Function<String, String> onQuery) {
        this.onQuery = onQuery;
        return this;
    }

    public ExecutionInfoFormatter onPreparedParameters(BiConsumer<SortedMap<String, String>, StringBuilder> onPreparedParameters) {
        this.onPreparedParameters = onPreparedParameters;
        return this;
    }

    public ExecutionInfoFormatter onCallableParameters(BiConsumer<SortedMap<String, String>, StringBuilder> onCallableParameters) {
        this.onCallableParameters = onCallableParameters;
        return this;
    }
}
