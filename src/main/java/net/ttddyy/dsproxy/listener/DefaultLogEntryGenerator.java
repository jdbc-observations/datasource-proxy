package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3
 */
public class DefaultLogEntryGenerator implements LogEntryGenerator {

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

        sb.append("Num:");
        sb.append(queryInfoList.size());
        sb.append(", ");

        sb.append("Query:");

        for (QueryInfo queryInfo : queryInfoList) {
            sb.append("{");
            final String query = queryInfo.getQuery();
            final List args = queryInfo.getQueryArgs();

            sb.append("[");
            sb.append(query);
            sb.append("][");

            for (Object arg : args) {
                sb.append(arg);
                sb.append(',');
            }

            // chop if last char is ','
            chompIfEndWith(sb, ',');

            sb.append("]");
            sb.append("} ");
        }

        return sb.toString();
    }

    private void chompIfEndWith(StringBuilder sb, char c) {
        final int lastCharIndex = sb.length() - 1;
        if (sb.charAt(lastCharIndex) == c) {
            sb.deleteCharAt(lastCharIndex);
        }
    }

}
