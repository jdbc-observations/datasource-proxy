package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;

/**
 * Default implementation of {@link QueryCountLogEntryCreator}.
 *
 * @author Tadaya Tsuyukubo
 */
public class DefaultQueryCountLogEntryCreator implements QueryCountLogEntryCreator {

    public String getLogMessage(String datasourceName, QueryCount queryCount) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Name:");
        sb.append(datasourceName == null ? "" : datasourceName);
        sb.append(", ");

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
        sb.append(", ");

        sb.append("Statement:");
        sb.append(queryCount.getStatement());
        sb.append(", ");

        sb.append("Prepared:");
        sb.append(queryCount.getPrepared());
        sb.append(", ");

        sb.append("Callable:");
        sb.append(queryCount.getCallable());

        return sb.toString();
    }

    @Override
    public String getLogMessageAsJson(String datasourceName, QueryCount queryCount) {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"name\":");
        if (datasourceName == null) {
            sb.append("null");
        } else {
            sb.append("\"");
            sb.append(datasourceName);
            sb.append("\"");
        }
        sb.append(", ");

        sb.append("\"time\":");
        sb.append(queryCount.getTime());
        sb.append(", ");

        sb.append("\"total\":");
        sb.append(queryCount.getTotal());
        sb.append(", ");

        sb.append("\"success\":");
        sb.append(queryCount.getSuccess());
        sb.append(", ");

        sb.append("\"failure\":");
        sb.append(queryCount.getFailure());
        sb.append(", ");

        sb.append("\"select\":");
        sb.append(queryCount.getSelect());
        sb.append(", ");

        sb.append("\"insert\":");
        sb.append(queryCount.getInsert());
        sb.append(", ");

        sb.append("\"update\":");
        sb.append(queryCount.getUpdate());
        sb.append(", ");

        sb.append("\"delete\":");
        sb.append(queryCount.getDelete());
        sb.append(", ");

        sb.append("\"other\":");
        sb.append(queryCount.getOther());
        sb.append(", ");

        sb.append("\"statement\":");
        sb.append(queryCount.getStatement());
        sb.append(", ");

        sb.append("\"prepared\":");
        sb.append(queryCount.getPrepared());
        sb.append(", ");

        sb.append("\"callable\":");
        sb.append(queryCount.getCallable());
        sb.append("}");
        return sb.toString();
    }
}
