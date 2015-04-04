package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;

/**
 * Default implementation of {@code QueryCountLogFormatter}.
 *
 * @author Tadaya Tsuyukubo
 */
public class DefaultQueryCountLogFormatter implements QueryCountLogFormatter {

    public String getLogMessage(String datasourceName, QueryCount queryCount) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Name:\"");
        sb.append(datasourceName);
        sb.append("\", ");

        sb.append("Time:");
        sb.append(queryCount.getTime());
        sb.append(", ");

        sb.append("Total:");
        sb.append(queryCount.getTotal());
        sb.append(", ");

        sb.append("Success:");
        sb.append(queryCount.getSuccess());
        sb.append(", ");

        sb.append("Failure:");
        sb.append(queryCount.getFailure());
        sb.append(", ");

        sb.append("Select:");
        sb.append(queryCount.getSelect());
        sb.append(", ");

        sb.append("Insert:");
        sb.append(queryCount.getInsert());
        sb.append(", ");

        sb.append("Update:");
        sb.append(queryCount.getUpdate());
        sb.append(", ");

        sb.append("Delete:");
        sb.append(queryCount.getDelete());
        sb.append(", ");

        sb.append("Other:");
        sb.append(queryCount.getOther());

        return sb.toString();
    }

}
