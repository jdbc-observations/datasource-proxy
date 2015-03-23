package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class DefaultLogEntryCreator implements LogEntryCreator {

    public String getLogEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName) {
        final boolean isSuccess = execInfo.getThrowable() == null;

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
        sb.append(isSuccess ? "True" : "False");
        sb.append(", ");

        sb.append("Type:");
        sb.append(getStatementType(execInfo.getStatementType()));
        sb.append(", ");


        // TODO: isBatch

        sb.append("QuerySize:");
        sb.append(queryInfoList.size());
        sb.append(", ");

        // TODO: batch size

        sb.append("Query:[");
        for (QueryInfo queryInfo : queryInfoList) {
            sb.append("(");
            sb.append(queryInfo.getQuery());
            sb.append("),");
        }
        chompIfEndWith(sb, ',');
        sb.append("], ");

        sb.append("Params:[");
        for (QueryInfo queryInfo : queryInfoList) {
            sb.append("(");
            for (Object arg : queryInfo.getQueryArgs()) {
                sb.append(arg);
                sb.append(',');
            }
            chompIfEndWith(sb, ',');
            sb.append("),");
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

}
