package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Create log entry in JSON format.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class DefaultJsonQueryLogEntryCreator extends AbstractQueryLogEntryCreator {

    @Override
    public String getLogEntry(QueryExecutionContext context, boolean writeDataSourceName, boolean writeConnectionId, boolean writeIsolation) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        if (writeDataSourceName) {
            writeDataSourceNameEntry(sb, context);
        }

        if (writeConnectionId) {
            writeConnectionIdEntry(sb, context);
        }

        if(writeIsolation) {
            writeIsolationEntry(sb, context);
        }

        // Time
        writeTimeEntry(sb, context);

        // Success
        writeResultEntry(sb, context);

        // Type
        writeTypeEntry(sb, context);

        // Batch
        writeBatchEntry(sb, context);

        // QuerySize
        writeQuerySizeEntry(sb, context);

        // BatchSize
        writeBatchSizeEntry(sb, context);

        // Queries
        writeQueriesEntry(sb, context);

        // Params
        writeParamsEntry(sb, context);

        return sb.toString();
    }

    /**
     * Write datasource name when enabled as json.
     *
     * <p>default: "name":"myDS",
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     */
    protected void writeDataSourceNameEntry(StringBuilder sb, QueryExecutionContext context) {
        String name = context.getDataSourceName();
        sb.append("\"name\":\"");
        sb.append(name == null ? "" : escapeSpecialCharacter(name));
        sb.append("\", ");
    }

    /**
     * Write connection ID when enabled as json.
     *
     * <p>default: "connection":1,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.4.2
     */
    protected void writeConnectionIdEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("\"connection\":");
        sb.append(context.getConnectionId());
        sb.append(", ");
    }

    /**
     * Write transaction isolation when enabled as json.
     *
     * <p>default: "isolation":"READ_COMMITTED",
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.8
     */
    protected void writeIsolationEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("\"isolation\":\"");
        sb.append(getTransactionIsolation(context.getIsolationLevel()));
        sb.append("\", ");
    }

    /**
     * Write elapsed time as json.
     *
     * <p>default: "time":123,
     *
     * The time is written in milliseconds.
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     */
    protected void writeTimeEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("\"time\":");
        sb.append(context.getElapsedTime().toMillis());
        sb.append(", ");
    }


    /**
     * Write query result whether successful or not as json.
     *
     * <p>default: "success":true,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     */
    protected void writeResultEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("\"success\":");
        sb.append(context.isSuccess() ? "true" : "false");
        sb.append(", ");
    }


    /**
     * Write statement type as json.
     *
     * <p>default: "type":"Prepared",
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     */
    protected void writeTypeEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("\"type\":\"");
        sb.append(getStatementType(context.getStatementType()));
        sb.append("\", ");
    }

    /**
     * Write whether batch execution or not as json.
     *
     * <p>default: "batch": true,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     */
    protected void writeBatchEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("\"batch\":");
        sb.append(context.isBatch() ? "true" : "false");
        sb.append(", ");
    }

    /**
     * Write query size as json.
     *
     * <p>default: "querySize":1,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     */
    protected void writeQuerySizeEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("\"querySize\":");
        sb.append(context.getQueries().size());
        sb.append(", ");
    }

    /**
     * Write batch size as json.
     *
     * <p>default: "batchSize":1,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     */
    protected void writeBatchSizeEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("\"batchSize\":");
        sb.append(context.getBatchSize());
        sb.append(", ");
    }

    /**
     * Write queries as json.
     *
     * <p>default: "query":["select 1","select 2"],
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     */
    protected void writeQueriesEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("\"query\":[");
        for (QueryInfo queryInfo : context.getQueries()) {
            sb.append("\"");
            sb.append(escapeSpecialCharacter(queryInfo.getQuery()));
            sb.append("\",");
        }
        chompIfEndWith(sb, ',');
        sb.append("], ");
    }

    /**
     * Write query parameters as json.
     *
     * <p>default for prepared: "params":[["foo","100"],["bar","101"]],
     * <p>default for callable: "params":[{"1":"foo","2":"100"},{"1":"bar","2":"101"}],
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     */
    protected void writeParamsEntry(StringBuilder sb, QueryExecutionContext context) {
        boolean isPrepared = context.getStatementType() == StatementType.PREPARED;
        sb.append("\"params\":[");
        for (QueryInfo queryInfo : context.getQueries()) {

            for (List<ParameterSetOperation> parameters : queryInfo.getParametersList()) {
                SortedMap<String, String> paramMap = getParametersToDisplay(parameters);
                // parameters per batch
                if (isPrepared) {
                    writeParamsForSinglePreparedEntry(sb, paramMap, context);
                } else {
                    writeParamsForSingleCallableEntry(sb, paramMap, context);
                }
            }
        }
        chompIfEndWith(sb, ',');
        sb.append("]");
        sb.append("}");
    }

    /**
     * Write parameters for single execution as json.
     *
     * <p>default: ["foo","100"],
     *
     * @param sb            StringBuilder to write
     * @param paramMap      sorted parameters map
     * @param context      query execution context
     */
    protected void writeParamsForSinglePreparedEntry(StringBuilder sb, SortedMap<String, String> paramMap, QueryExecutionContext context) {
        sb.append("[");
        for (Map.Entry<String, String> paramEntry : paramMap.entrySet()) {
            Object value = paramEntry.getValue();
            if (value == null) {
                sb.append("null");
            } else {
                sb.append("\"");
                sb.append(escapeSpecialCharacter(value.toString()));
                sb.append("\"");
            }
            sb.append(",");
        }
        chompIfEndWith(sb, ',');
        sb.append("],");
    }

    /**
     * Write parameters for single execution.
     *
     * <p>default: {"1"="foo","bar"="100"},
     *
     * @param sb            StringBuilder to write
     * @param paramMap      sorted parameters map
     * @param context      query execution context
     */
    protected void writeParamsForSingleCallableEntry(StringBuilder sb, Map<String, String> paramMap, QueryExecutionContext context) {
        sb.append("{");
        for (Map.Entry<String, String> paramEntry : paramMap.entrySet()) {
            String key = paramEntry.getKey();
            Object value = paramEntry.getValue();
            sb.append("\"");
            sb.append(escapeSpecialCharacter(key));
            sb.append("\":");
            if (value == null) {
                sb.append("null");
            } else {
                sb.append("\"");
                sb.append(escapeSpecialCharacter(value.toString()));
                sb.append("\"");
            }
            sb.append(",");
        }
        chompIfEndWith(sb, ',');
        sb.append("},");
    }

    protected String escapeSpecialCharacter(String input) {
        if (input == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            String value = JSON_SPECIAL_CHARS.get(c);
            if (value != null) {
                sb.append(value);
            } else if (c < 0x20) {
                // Control character that does not have a short sequence
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                // Short sequence
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
