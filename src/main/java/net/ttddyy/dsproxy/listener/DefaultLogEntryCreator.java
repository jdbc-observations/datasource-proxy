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

    public String getLogEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName) {
        final StringBuilder sb = new StringBuilder();

        if (writeDataSourceName) {
            sb.append("Name:");
            sb.append(execInfo.getDataSourceName());
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
