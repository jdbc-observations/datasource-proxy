package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class DefaultJsonQueryLogEntryCreator extends AbstractQueryLogEntryCreator {

    @Override
    public String getLogEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        if (writeDataSourceName) {
            writeDataSourceNameEntryForJson(sb, execInfo, queryInfoList);
        }


        // Time
        writeTimeEntryForJson(sb, execInfo, queryInfoList);

        // Success
        writeResultEntryForJson(sb, execInfo, queryInfoList);

        // Type
        writeTypeEntryForJson(sb, execInfo, queryInfoList);

        // Batch
        writeBatchEntryForJson(sb, execInfo, queryInfoList);

        // QuerySize
        writeQuerySizeEntryForJson(sb, execInfo, queryInfoList);

        // BatchSize
        writeBatchSizeEntryForJson(sb, execInfo, queryInfoList);

        // Queries
        writeQueriesEntryForJson(sb, execInfo, queryInfoList);

        // Params
        writeParamsEntryForJson(sb, execInfo, queryInfoList);

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
     * @since 1.3.3
     */
    protected void writeDataSourceNameEntryForJson(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        String name = execInfo.getDataSourceName();
        sb.append("\"name\":\"");
        sb.append(name == null ? "" : escapeSpecialCharacterForJson(name));
        sb.append("\", ");
    }

    /**
     * Write elapsed time as json.
     *
     * <p>default: "time":123,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeTimeEntryForJson(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @since 1.3.3
     */
    protected void writeResultEntryForJson(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @since 1.3.3
     */
    protected void writeTypeEntryForJson(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @since 1.3.3
     */
    protected void writeBatchEntryForJson(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @since 1.3.3
     */
    protected void writeQuerySizeEntryForJson(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @since 1.3.3
     */
    protected void writeBatchSizeEntryForJson(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @since 1.3.3
     */
    protected void writeQueriesEntryForJson(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("\"query\":[");
        for (QueryInfo queryInfo : queryInfoList) {
            sb.append("\"");
            sb.append(escapeSpecialCharacterForJson(queryInfo.getQuery()));
            sb.append("\",");
        }
        chompIfEndWith(sb, ',');
        sb.append("], ");
    }

    /**
     * Write query parameters as json.
     *
     * <p>default: "params":[{"1":"foo","2":"100"},{"1":"bar","2":"101"}],
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeParamsEntryForJson(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("\"params\":[");
        for (QueryInfo queryInfo : queryInfoList) {

            for (List<ParameterSetOperation> parameters : queryInfo.getParametersList()) {
                Map<String, String> paramMap = getParametersToDisplay(parameters);
                // parameters per batch
                writeParamsForSingleEntryForJson(sb, paramMap, execInfo, queryInfoList);
            }
        }
        chompIfEndWith(sb, ',');
        sb.append("]");
        sb.append("}");
    }

    /**
     * Write parameters for single execution as json.
     *
     * <p>default: {"1":"foo","2":"100"},
     *
     * @param sb            StringBuilder to write
     * @param paramMap      parameters map
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeParamsForSingleEntryForJson(StringBuilder sb, Map<String, String> paramMap, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // sort
        SortedMap<String, String> sortedParamMap = new TreeMap<String, String>(new StringAsIntegerComparator());
        sortedParamMap.putAll(paramMap);


        sb.append("{");
        for (Map.Entry<String, String> paramEntry : sortedParamMap.entrySet()) {
            String key = paramEntry.getKey();
            Object value = paramEntry.getValue();
            sb.append("\"");
            sb.append(escapeSpecialCharacterForJson(key));
            sb.append("\":");
            if (value == null) {
                sb.append("null");
            } else {
                sb.append("\"");
                sb.append(escapeSpecialCharacterForJson(value.toString()));
                sb.append("\"");
            }
            sb.append(",");
        }
        chompIfEndWith(sb, ',');
        sb.append("},");
    }

    protected String getStatementType(StatementType statementType) {
        if (StatementType.STATEMENT.equals(statementType)) {
            return "Statement";
        } else if (StatementType.PREPARED.equals(statementType)) {
            return "Prepared";
        } else if (StatementType.CALLABLE.equals(statementType)) {
            return "Callable";
        }
        return "Unknown";
    }

    protected void chompIfEndWith(StringBuilder sb, char c) {
        final int lastCharIndex = sb.length() - 1;
        if (sb.charAt(lastCharIndex) == c) {
            sb.deleteCharAt(lastCharIndex);
        }
    }

    protected String escapeSpecialCharacterForJson(String input) {
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


    /**
     * @return parameterIndex or parameterName as String
     * @since 1.4
     */
    public String getParameterKeyToDisplay(ParameterSetOperation param) {
        Object key = param.getArgs()[0];  // either int(parameterIndex) or string(parameterName)
        return key instanceof String ? (String) key : key.toString();
    }

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

}
