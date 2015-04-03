package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;

import java.util.*;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class DefaultLogEntryCreator implements LogEntryCreator {

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
            String name = execInfo.getDataSourceName();
            sb.append("Name:");
            sb.append(name == null ? "" : name);
            sb.append(", ");
        }

        sb.append("Time:");
        sb.append(execInfo.getElapsedTime());
        sb.append(", ");

        sb.append("Success:");
        sb.append(execInfo.isSuccess() ? "True" : "False");
        sb.append(", ");

        sb.append("Type:");
        sb.append(getStatementType(execInfo.getStatementType()));
        sb.append(", ");

        sb.append("Batch:");
        sb.append(execInfo.isBatch() ? "True" : "False");
        sb.append(", ");

        sb.append("QuerySize:");
        sb.append(queryInfoList.size());
        sb.append(", ");

        sb.append("BatchSize:");
        sb.append(execInfo.getBatchSize());
        sb.append(", ");

        sb.append("Query:[");
        for (QueryInfo queryInfo : queryInfoList) {
            sb.append("\"");
            sb.append(queryInfo.getQuery());
            sb.append("\",");
        }
        chompIfEndWith(sb, ',');
        sb.append("], ");

        sb.append("Params:[");
        for (QueryInfo queryInfo : queryInfoList) {
            for (Map<String, Object> paramMap : queryInfo.getQueryArgsList()) {

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
        }
        chompIfEndWith(sb, ',');
        sb.append("]");

        return sb.toString();
    }

    @Override
    public String getLogEntryAsJson(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        if (writeDataSourceName) {
            String name = execInfo.getDataSourceName();
            sb.append("\"name\":\"");
            sb.append(name == null ? "" : escapeSpecialCharacterForJson(name));
            sb.append("\", ");
        }

        sb.append("\"time\":");
        sb.append(execInfo.getElapsedTime());
        sb.append(", ");

        sb.append("\"success\":");
        sb.append(execInfo.isSuccess() ? "true" : "false");
        sb.append(", ");

        sb.append("\"type\":\"");
        sb.append(getStatementType(execInfo.getStatementType()));
        sb.append("\", ");

        sb.append("\"batch\":");
        sb.append(execInfo.isBatch() ? "true" : "false");
        sb.append(", ");

        sb.append("\"querySize\":");
        sb.append(queryInfoList.size());
        sb.append(", ");

        sb.append("\"batchSize\":");
        sb.append(execInfo.getBatchSize());
        sb.append(", ");

        sb.append("\"query\":[");
        for (QueryInfo queryInfo : queryInfoList) {
            sb.append("\"");
            sb.append(escapeSpecialCharacterForJson(queryInfo.getQuery()));
            sb.append("\",");
        }
        chompIfEndWith(sb, ',');
        sb.append("], ");

        sb.append("\"params\":[");
        for (QueryInfo queryInfo : queryInfoList) {
            for (Map<String, Object> paramMap : queryInfo.getQueryArgsList()) {

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
        }
        chompIfEndWith(sb, ',');
        sb.append("]");
        sb.append("}");

        return sb.toString();
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
    private static class StringAsIntegerComparator implements Comparator<String> {
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
