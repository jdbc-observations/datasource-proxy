package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.StatementType;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class DefaultQueryLogEntryCreator extends AbstractQueryLogEntryCreator {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private boolean multiline = false;

    @Override
    public String getLogEntry(QueryExecutionContext context, boolean writeDataSourceName, boolean writeConnectionId, boolean writeIsolation) {
        final StringBuilder sb = new StringBuilder();

        if (this.multiline) {
            sb.append(LINE_SEPARATOR);
        }

        if (writeDataSourceName) {
            writeDataSourceNameEntry(sb, context);
        }

        if (writeConnectionId) {
            writeConnectionIdEntry(sb, context);
        }

        if(writeIsolation) {
            writeIsolationEntry(sb, context);
        }

        // Time
        writeTimeEntry(sb, context);

        // Success
        writeResultEntry(sb, context);

        if (this.multiline) {
            sb.delete(sb.length() - 2, sb.length());  // delete last ", "
            sb.append(LINE_SEPARATOR);
        }

        // Type
        writeTypeEntry(sb, context);

        // Batch
        writeBatchEntry(sb, context);

        // QuerySize
        writeQuerySizeEntry(sb, context);

        // BatchSize
        writeBatchSizeEntry(sb, context);

        if (this.multiline) {
            sb.delete(sb.length() - 2, sb.length());  // delete last ", "
            sb.append(LINE_SEPARATOR);
        }

        // Queries
        writeQueriesEntry(sb, context);

        if (this.multiline) {
            sb.delete(sb.length() - 2, sb.length());  // delete last ", "
            sb.append(LINE_SEPARATOR);
        }

        // Params
        writeParamsEntry(sb, context);

        return sb.toString();
    }

    /**
     * Write datasource name when enabled.
     *
     * <p>default: Name: myDS,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.3.3
     */
    protected void writeDataSourceNameEntry(StringBuilder sb, QueryExecutionContext context) {
        String name = context.getDataSourceName();
        sb.append("Name:");
        sb.append(name == null ? "" : name);
        sb.append(", ");
    }

    /**
     * Write connection ID when enabled.
     *
     * <p>default: Connection: 1,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.4.2
     */
    protected void writeConnectionIdEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("Connection:");
        sb.append(context.getConnectionId());
        sb.append(", ");
    }

    /**
     * Write transaction isolation when enabled.
     *
     * <p>default: Isolation: READ_COMMITTED,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.8
     */
    protected void writeIsolationEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("Isolation:");
        sb.append(getTransactionIsolation(context.getIsolationLevel()));
        sb.append(", ");
    }

    /**
     * Write elapsed time.
     *
     * <p>default: Time: 123,
     *
     * The time is written in milliseconds.
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.3.3
     */
    protected void writeTimeEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("Time:");
        sb.append(context.getElapsedTime().toMillis());
        sb.append(", ");
    }

    /**
     * Write query result whether successful or not.
     *
     * <p>default: Success: True,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.3.3
     */
    protected void writeResultEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("Success:");
        sb.append(context.isSuccess() ? "True" : "False");
        sb.append(", ");
    }

    /**
     * Write statement type.
     *
     * <p>default: Type: Prepared,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.3.3
     */
    protected void writeTypeEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("Type:");
        sb.append(getStatementType(context.getStatementType()));
        sb.append(", ");
    }

    /**
     * Write whether batch execution or not.
     *
     * <p>default: Batch: True,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.3.3
     */
    protected void writeBatchEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("Batch:");
        sb.append(context.isBatch() ? "True" : "False");
        sb.append(", ");
    }

    /**
     * Write query size.
     *
     * <p>default: QuerySize: 1,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.3.3
     */
    protected void writeQuerySizeEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("QuerySize:");
        sb.append(context.getQueries().size());
        sb.append(", ");
    }

    /**
     * Write batch size.
     *
     * <p>default: BatchSize: 1,
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.3.3
     */
    protected void writeBatchSizeEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("BatchSize:");
        sb.append(context.getBatchSize());
        sb.append(", ");
    }

    /**
     * Write queries.
     *
     * <p>default: Query:["select 1", "select 2"],
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.3.3
     */
    protected void writeQueriesEntry(StringBuilder sb, QueryExecutionContext context) {
        sb.append("Query:[");
        for (QueryInfo queryInfo : context.getQueries()) {
            sb.append("\"");
            sb.append(formatQuery(queryInfo.getQuery()));
            sb.append("\",");
        }
        chompIfEndWith(sb, ',');
        sb.append("], ");
    }

    /**
     * Callback method that allows the given query to be reformatted for logging.
     *
     * <p>Subclasses can override this method to change the query, for example, by calling Hibernate's
     * {@code BasicFormatterImpl}.
     *
     * @param query a query to format
     * @return formatted query
     * @since 1.4.1
     */
    protected String formatQuery(String query) {
        return query;
    }

    /**
     * Write query parameters.
     *
     * <p>Default format for prepared statements: {@code Params:[(foo,100),(bar,101)]}.
     * <p>Default format for callable statements: {@code Params:[(1=foo,key=100),(1=bar,key=101)]}.
     *
     * @param sb            StringBuilder to write
     * @param context      query execution context
     * @since 1.3.3
     */
    protected void writeParamsEntry(StringBuilder sb, QueryExecutionContext context) {

        boolean isPrepared = context.getStatementType() == StatementType.PREPARED;

        sb.append("Params:[");

        for (QueryInfo queryInfo : context.getQueries()) {
            for (List<ParameterSetOperation> parameters : queryInfo.getParametersList()) {
                SortedMap<String, String> paramMap = getParametersToDisplay(parameters);

                // parameters per batch.
                //   for prepared: (val1,val2,...)
                //   for callable: (key1=val1,key2=val2,...)
                if (isPrepared) {
                    writeParamsForSinglePreparedEntry(sb, paramMap, context);
                } else {
                    writeParamsForSingleCallableEntry(sb, paramMap, context);
                }

            }
        }

        chompIfEndWith(sb, ',');
        sb.append("]");
    }

    /**
     * Write query parameters for PreparedStatement.
     *
     * <p>default: Params:[(foo,100),(bar,101)],
     *
     * @param sb            StringBuilder to write
     * @param paramMap      sorted parameters map
     * @param context      query execution context
     * @since 1.4
     */
    protected void writeParamsForSinglePreparedEntry(StringBuilder sb, SortedMap<String, String> paramMap, QueryExecutionContext context) {
        sb.append("(");
        for (Map.Entry<String, String> paramEntry : paramMap.entrySet()) {
            sb.append(paramEntry.getValue());
            sb.append(",");
        }
        chompIfEndWith(sb, ',');
        sb.append("),");
    }

    /**
     * Write parameters for single execution.
     *
     * <p>default: (1=foo,bar=100),
     *
     * @param sb            StringBuilder to write
     * @param paramMap      sorted parameters map
     * @param context      query execution context
     * @since 1.4
     */
    protected void writeParamsForSingleCallableEntry(StringBuilder sb, SortedMap<String, String> paramMap, QueryExecutionContext context) {
        sb.append("(");
        for (Map.Entry<String, String> paramEntry : paramMap.entrySet()) {
            sb.append(paramEntry.getKey());
            sb.append("=");
            sb.append(paramEntry.getValue());
            sb.append(",");
        }
        chompIfEndWith(sb, ',');
        sb.append("),");
    }


    /**
     * Enable multiline output in {@link #getLogEntry(QueryExecutionContext, boolean, boolean, boolean)}.
     *
     * @param multiline return multi lined log entry when true is set
     * @since 1.4.1
     */
    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    /**
     * @return true if multiline output is enabled
     * @since 1.4.1
     */
    public boolean isMultiline() {
        return multiline;
    }
}
