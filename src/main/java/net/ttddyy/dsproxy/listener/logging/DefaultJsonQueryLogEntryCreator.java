package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
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
    public String getLogEntry(ExecutionInfo execInfo, boolean writeDataSourceName, boolean writeConnectionId) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        if (writeDataSourceName) {
            writeDataSourceNameEntry(sb, execInfo);
        }

        if (writeConnectionId) {
            writeConnectionIdEntry(sb, execInfo);
        }


        // Time
        writeTimeEntry(sb, execInfo);

        // Success
        writeResultEntry(sb, execInfo);

        // Type
        writeTypeEntry(sb, execInfo);

        // Batch
        writeBatchEntry(sb, execInfo);

        // QuerySize
        writeQuerySizeEntry(sb, execInfo);

        // BatchSize
        writeBatchSizeEntry(sb, execInfo);

        // Queries
        writeQueriesEntry(sb, execInfo);

        // Params
        writeParamsEntry(sb, execInfo);

        return sb.toString();
    }

    /**
     * Write datasource name when enabled as json.
     *
     * <p>default: "name":"myDS",
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     */
    protected void writeDataSourceNameEntry(StringBuilder sb, ExecutionInfo execInfo) {
        String name = execInfo.getDataSourceName();
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
     * @param execInfo      execution info
     * @since 1.4.2
     */
    protected void writeConnectionIdEntry(StringBuilder sb, ExecutionInfo execInfo) {
        sb.append("\"connection\":");
        sb.append(execInfo.getConnectionId());
        sb.append(", ");
    }

    /**
     * Write elapsed time as json.
     *
     * <p>default: "time":123,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     */
    protected void writeTimeEntry(StringBuilder sb, ExecutionInfo execInfo) {
        sb.append("\"time\":");
        sb.append(execInfo.getElapsedTime());
        sb.append(", ");
    }


    /**
     * Write query result whether successful or not as json.
     *
     * <p>default: "success":true,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     */
    protected void writeResultEntry(StringBuilder sb, ExecutionInfo execInfo) {
        sb.append("\"success\":");
        sb.append(execInfo.isSuccess() ? "true" : "false");
        sb.append(", ");
    }


    /**
     * Write statement type as json.
     *
     * <p>default: "type":"Prepared",
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     */
    protected void writeTypeEntry(StringBuilder sb, ExecutionInfo execInfo) {
        sb.append("\"type\":\"");
        sb.append(getStatementType(execInfo.getStatementType()));
        sb.append("\", ");
    }

    /**
     * Write whether batch execution or not as json.
     *
     * <p>default: "batch": true,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     */
    protected void writeBatchEntry(StringBuilder sb, ExecutionInfo execInfo) {
        sb.append("\"batch\":");
        sb.append(execInfo.isBatch() ? "true" : "false");
        sb.append(", ");
    }

    /**
     * Write query size as json.
     *
     * <p>default: "querySize":1,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     */
    protected void writeQuerySizeEntry(StringBuilder sb, ExecutionInfo execInfo) {
        sb.append("\"querySize\":");
        sb.append(execInfo.getQueries().size());
        sb.append(", ");
    }

    /**
     * Write batch size as json.
     *
     * <p>default: "batchSize":1,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     */
    protected void writeBatchSizeEntry(StringBuilder sb, ExecutionInfo execInfo) {
        sb.append("\"batchSize\":");
        sb.append(execInfo.getBatchSize());
        sb.append(", ");
    }

    /**
     * Write queries as json.
     *
     * <p>default: "query":["select 1","select 2"],
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     */
    protected void writeQueriesEntry(StringBuilder sb, ExecutionInfo execInfo) {
        sb.append("\"query\":[");
        for (QueryInfo queryInfo : execInfo.getQueries()) {
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
     * @param execInfo      execution info
     */
    protected void writeParamsEntry(StringBuilder sb, ExecutionInfo execInfo) {
        boolean isPrepared = execInfo.getStatementType() == StatementType.PREPARED;
        sb.append("\"params\":[");
        for (QueryInfo queryInfo : execInfo.getQueries()) {

            for (List<ParameterSetOperation> parameters : queryInfo.getParametersList()) {
                SortedMap<String, String> paramMap = getParametersToDisplay(parameters);
                // parameters per batch
                if (isPrepared) {
                    writeParamsForSinglePreparedEntry(sb, paramMap, execInfo);
                } else {
                    writeParamsForSingleCallableEntry(sb, paramMap, execInfo);
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
     * @param execInfo      execution info
     */
    protected void writeParamsForSinglePreparedEntry(StringBuilder sb, SortedMap<String, String> paramMap, ExecutionInfo execInfo) {
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
     * @param execInfo      execution info
     */
    protected void writeParamsForSingleCallableEntry(StringBuilder sb, Map<String, String> paramMap, ExecutionInfo execInfo) {
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
            sb.append(value != null ? value : c);
        }
        return sb.toString();
    }

}
