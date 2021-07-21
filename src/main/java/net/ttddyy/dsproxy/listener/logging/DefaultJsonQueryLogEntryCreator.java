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
    public String getLogEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName, boolean writeConnectionId, boolean writeIsolation) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        if (writeDataSourceName) {
            writeDataSourceNameEntry(sb, execInfo, queryInfoList);
        }

        if (writeConnectionId) {
            writeConnectionIdEntry(sb, execInfo, queryInfoList);
        }

        if(writeIsolation) {
            writeIsolationEntry(sb, execInfo, queryInfoList);
        }

        // Time
        writeTimeEntry(sb, execInfo, queryInfoList);

        // Success
        writeResultEntry(sb, execInfo, queryInfoList);

        // Type
        writeTypeEntry(sb, execInfo, queryInfoList);

        // Batch
        writeBatchEntry(sb, execInfo, queryInfoList);

        // QuerySize
        writeQuerySizeEntry(sb, execInfo, queryInfoList);

        // BatchSize
        writeBatchSizeEntry(sb, execInfo, queryInfoList);

        // Queries
        writeQueriesEntry(sb, execInfo, queryInfoList);

        // Params
        writeParamsEntry(sb, execInfo, queryInfoList);

        return sb.toString();
    }

    /**
     * Write datasource name when enabled as json.
     *
     * <p>default: "name":"myDS",
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     */
    protected void writeDataSourceNameEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @param queryInfoList query info list
     * @since 1.4.2
     */
    protected void writeConnectionIdEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("\"connection\":");
        sb.append(execInfo.getConnectionId());
        sb.append(", ");
    }

    /**
     * Write transaction isolation when enabled as json.
     *
     * <p>default: "isolation":"READ_COMMITTED",
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.8
     */
    protected void writeIsolationEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("\"isolation\":\"");
        sb.append(getTransactionIsolation(execInfo.getIsolationLevel()));
        sb.append("\", ");
    }

    /**
     * Write elapsed time as json.
     *
     * <p>default: "time":123,
     *
     * The unit of time is determined by underlying {@link net.ttddyy.dsproxy.proxy.Stopwatch} implementation.
     * (milli vs nano seconds)
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     */
    protected void writeTimeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @param queryInfoList query info list
     */
    protected void writeResultEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @param queryInfoList query info list
     */
    protected void writeTypeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @param queryInfoList query info list
     */
    protected void writeBatchEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @param queryInfoList query info list
     */
    protected void writeQuerySizeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("\"querySize\":");
        sb.append(queryInfoList.size());
        sb.append(", ");
    }

    /**
     * Write batch size as json.
     *
     * <p>default: "batchSize":1,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     */
    protected void writeBatchSizeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @param queryInfoList query info list
     */
    protected void writeQueriesEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("\"query\":[");
        for (QueryInfo queryInfo : queryInfoList) {
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
     * @param queryInfoList query info list
     */
    protected void writeParamsEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        boolean isPrepared = execInfo.getStatementType() == StatementType.PREPARED;
        sb.append("\"params\":[");
        for (QueryInfo queryInfo : queryInfoList) {

            for (List<ParameterSetOperation> parameters : queryInfo.getParametersList()) {
                SortedMap<String, String> paramMap = getParametersToDisplay(parameters);
                // parameters per batch
                if (isPrepared) {
                    writeParamsForSinglePreparedEntry(sb, paramMap, execInfo, queryInfoList);
                } else {
                    writeParamsForSingleCallableEntry(sb, paramMap, execInfo, queryInfoList);
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
     * @param queryInfoList query info list
     */
    protected void writeParamsForSinglePreparedEntry(StringBuilder sb, SortedMap<String, String> paramMap, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @param queryInfoList query info list
     */
    protected void writeParamsForSingleCallableEntry(StringBuilder sb, Map<String, String> paramMap, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
