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
        sb.append("DataSource:");
        sb.append(datasourceName);
        sb.append(" ");

        sb.append("ElapsedTime:");
        sb.append(queryCount.getElapsedTime());
        sb.append(" ");

        sb.append("Call:");
        sb.append(queryCount.getCall());
        sb.append(" ");

        sb.append("Query:");
        sb.append(queryCount.getTotalNumOfQuery());

        sb.append(" (");
        sb.append("Select:");
        sb.append(queryCount.getSelect());
        sb.append(" ");

        sb.append("Insert:");
        sb.append(queryCount.getInsert());
        sb.append(" ");

        sb.append("Update:");
        sb.append(queryCount.getUpdate());
        sb.append(" ");

        sb.append("Delete:");
        sb.append(queryCount.getDelete());
        sb.append(" ");

        sb.append("Other:");
        sb.append(queryCount.getOther());
        sb.append(")");

        return sb.toString();
    }

}
