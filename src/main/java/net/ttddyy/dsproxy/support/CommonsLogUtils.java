package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;

/**
 * @author Tadaya Tsuyukubo
 */
public class CommonsLogUtils {

    public static String getCountLogMessage(QueryCount counter, String dataSourceName) {

        final StringBuilder sb = new StringBuilder();
        sb.append("DataSource:");
        sb.append(dataSourceName);
        sb.append(" ");

        sb.append("ElapsedTime:");
        sb.append(counter.getElapsedTime());
        sb.append(" ");

        sb.append("Call:");
        sb.append(counter.getCall());
        sb.append(" ");

        sb.append("Query:");
        sb.append(counter.getTotalNumOfQuery());

        sb.append(" (");
        sb.append("Select:");
        sb.append(counter.getSelect());
        sb.append(" ");

        sb.append("Insert:");
        sb.append(counter.getInsert());
        sb.append(" ");

        sb.append("Update:");
        sb.append(counter.getUpdate());
        sb.append(" ");

        sb.append("Delete:");
        sb.append(counter.getDelete());
        sb.append(" ");

        sb.append("Other:");
        sb.append(counter.getOther());
        sb.append(")");

        return sb.toString();
    }

}
