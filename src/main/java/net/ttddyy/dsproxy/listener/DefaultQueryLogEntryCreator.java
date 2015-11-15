package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;

import java.util.*;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class DefaultQueryLogEntryCreator implements QueryLogEntryCreator {

    protected static final Map<Character, String> JSON_SPECIAL_CHARS = new HashMap<Character, String>();

    static {
        JSON_SPECIAL_CHARS.put('"', "\\\"");   // quotation mark
        JSON_SPECIAL_CHARS.put('\\', "\\\\");  // reverse solidus
        JSON_SPECIAL_CHARS.put('/', "\\/");    // solidus
        JSON_SPECIAL_CHARS.put('\b', "\\b");   // backspace
        JSON_SPECIAL_CHARS.put('\f', "\\f");   // formfeed
        JSON_SPECIAL_CHARS.put('\n', "\\n");   // newline
        JSON_SPECIAL_CHARS.put('\r', "\\r");   // carriage return
        JSON_SPECIAL_CHARS.put('\t', "\\t");   // horizontal tab
    }

    @Override
    public String getLogEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName) {
        final StringBuilder sb = new StringBuilder();

        if (writeDataSourceName) {
            writeDataSourceNameEntry(sb, execInfo, queryInfoList);
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
     * Write datasource name when enabled.
     *
     * <p>default: Name: myDS,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeDataSourceNameEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        String name = execInfo.getDataSourceName();
        sb.append("Name:");
        sb.append(name == null ? "" : name);
        sb.append(", ");
    }

    /**
     * Write elapsed time.
     *
     * <p>default: Time: 123,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeTimeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("Time:");
        sb.append(execInfo.getElapsedTime());
        sb.append(", ");
    }

    /**
     * Write query result whether successful or not.
     *
     * <p>default: Success: True,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeResultEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("Success:");
        sb.append(execInfo.isSuccess() ? "True" : "False");
        sb.append(", ");
    }

    /**
     * Write statement type.
     *
     * <p>default: Type: Prepared,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeTypeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("Type:");
        sb.append(getStatementType(execInfo.getStatementType()));
        sb.append(", ");
    }

    /**
     * Write whether batch execution or not.
     *
     * <p>default: Batch: True,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeBatchEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("Batch:");
        sb.append(execInfo.isBatch() ? "True" : "False");
        sb.append(", ");
    }

    /**
     * Write query size.
     *
     * <p>default: QuerySize: 1,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeQuerySizeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("QuerySize:");
        sb.append(queryInfoList.size());
        sb.append(", ");
    }

    /**
     * Write batch size.
     *
     * <p>default: BatchSize: 1,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeBatchSizeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("BatchSize:");
        sb.append(execInfo.getBatchSize());
        sb.append(", ");
    }

    /**
     * Write queries.
     *
     * <p>default: Query:["select 1", "select 2"],
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeQueriesEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("Query:[");
        for (QueryInfo queryInfo : queryInfoList) {
            sb.append("\"");
            sb.append(queryInfo.getQuery());
            sb.append("\",");
        }
        chompIfEndWith(sb, ',');
        sb.append("], ");
    }

    /**
     * Write query parameters.
     *
     * <p>default: Params:[(1=foo,2=100),(1=bar,2=101)],
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeParamsEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("Params:[");
        for (QueryInfo queryInfo : queryInfoList) {
            for (Map<String, Object> paramMap : queryInfo.getQueryArgsList()) {

                // parameters per batch
                writeParamsForSingleEntry(sb, paramMap, execInfo, queryInfoList);
            }
        }
        chompIfEndWith(sb, ',');
        sb.append("]");
    }

    /**
     * Write parameters for single execution.
     *
     * <p>default: (1=foo,2=100),
     *
     * @param sb            StringBuilder to write
     * @param paramMap      parameters map
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeParamsForSingleEntry(StringBuilder sb, Map<String, Object> paramMap, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // sort
        SortedMap<String, Object> sortedParamMap = new TreeMap<String, Object>(new StringAsIntegerComparator());
        sortedParamMap.putAll(paramMap);

        sb.append("(");
        for (Map.Entry<String, Object> paramEntry : sortedParamMap.entrySet()) {
            sb.append(paramEntry.getKey());
            sb.append("=");
            sb.append(paramEntry.getValue());
            sb.append(",");
        }
        chompIfEndWith(sb, ',');
        sb.append("),");
    }

    @Override
    public String getLogEntryAsJson(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName) {
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
            for (Map<String, Object> paramMap : queryInfo.getQueryArgsList()) {

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
    protected void writeParamsForSingleEntryForJson(StringBuilder sb, Map<String, Object> paramMap, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        // sort
        SortedMap<String, Object> sortedParamMap = new TreeMap<String, Object>(new StringAsIntegerComparator());
        sortedParamMap.putAll(paramMap);

        sb.append("{");
        for (Map.Entry<String, Object> paramEntry : sortedParamMap.entrySet()) {
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
     * Comparator considering string as integer.
     *
     * When it has null, put it as first element(smaller).
     * If string cannot be parsed to integer, it compared as string.
     */
    public static class StringAsIntegerComparator implements Comparator<String> {
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
                return Integer.compare(Integer.parseInt(left), Integer.parseInt(right));
            } catch (NumberFormatException e) {
                return left.compareTo(right);  // use String comparison
            }
        }
    }
}
