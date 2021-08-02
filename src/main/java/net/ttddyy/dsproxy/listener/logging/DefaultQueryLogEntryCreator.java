package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
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
    public String getLogEntry(ExecutionInfo execInfo, List<QueryInfo> queryInfoList, boolean writeDataSourceName, boolean writeConnectionId, boolean writeIsolation) {
        final StringBuilder sb = new StringBuilder();

        if (this.multiline) {
            sb.append(LINE_SEPARATOR);
        }

        if (writeDataSourceName) {
            writeDataSourceNameEntry(sb, execInfo, queryInfoList);
        }

        if (writeConnectionId) {
            writeConnectionIdEntry(sb, execInfo, queryInfoList);
        }

        if(writeIsolation) {
            writeIsolationEntry(sb, execInfo, queryInfoList);
        }

        // Time
        writeTimeEntry(sb, execInfo, queryInfoList);

        // Success
        writeResultEntry(sb, execInfo, queryInfoList);

        if (this.multiline) {
            sb.delete(sb.length() - 2, sb.length());  // delete last ", "
            sb.append(LINE_SEPARATOR);
        }

        // Type
        writeTypeEntry(sb, execInfo, queryInfoList);

        // Batch
        writeBatchEntry(sb, execInfo, queryInfoList);

        // QuerySize
        writeQuerySizeEntry(sb, execInfo, queryInfoList);

        // BatchSize
        writeBatchSizeEntry(sb, execInfo, queryInfoList);

        if (this.multiline) {
            sb.delete(sb.length() - 2, sb.length());  // delete last ", "
            sb.append(LINE_SEPARATOR);
        }

        // Queries
        writeQueriesEntry(sb, execInfo, queryInfoList);

        if (this.multiline) {
            sb.delete(sb.length() - 2, sb.length());  // delete last ", "
            sb.append(LINE_SEPARATOR);
        }

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
     * Write connection ID when enabled.
     *
     * <p>default: Connection: 1,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.4.2
     */
    protected void writeConnectionIdEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("Connection:");
        sb.append(execInfo.getConnectionId());
        sb.append(", ");
    }

    /**
     * Write transaction isolation when enabled.
     *
     * <p>default: Isolation: READ_COMMITTED,
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.8
     */
    protected void writeIsolationEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("Isolation:");
        sb.append(getTransactionIsolation(execInfo.getIsolationLevel()));
        sb.append(", ");
    }

    /**
     * Write elapsed time.
     *
     * <p>default: Time: 123,
     *
     * The unit of time is determined by underlying {@link net.ttddyy.dsproxy.proxy.Stopwatch} implementation.
     * (milli vs nano seconds)
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
            sb.append(formatQuery(queryInfo.getQuery()));
            sb.append("\",");
        }
        chompIfEndWith(sb, ',');
        sb.append("], ");
    }

    /**
     * Callback method to allow alternating given query for logging.
     *
     * Subclass can override this method to change the given query.
     * For example, it can call BasicFormatterImpl in hibernate to format the query.
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
     * <p>default for prepared: Params:[(foo,100),(bar,101)],
     * <p>default for callable: Params:[(1=foo,key=100),(1=bar,key=101)],
     *
     * @param sb            StringBuilder to write
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.3.3
     */
    protected void writeParamsEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {

        boolean isPrepared = execInfo.getStatementType() == StatementType.PREPARED;

        sb.append("Params:[");

        for (QueryInfo queryInfo : queryInfoList) {
            for (List<ParameterSetOperation> parameters : queryInfo.getParametersList()) {
                SortedMap<String, String> paramMap = getParametersToDisplay(parameters);

                // parameters per batch.
                //   for prepared: (val1,val2,...)
                //   for callable: (key1=val1,key2=val2,...)
                if (isPrepared) {
                    writeParamsForSinglePreparedEntry(sb, paramMap, execInfo, queryInfoList);
                } else {
                    writeParamsForSingleCallableEntry(sb, paramMap, execInfo, queryInfoList);
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
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.4
     */
    protected void writeParamsForSinglePreparedEntry(StringBuilder sb, SortedMap<String, String> paramMap, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * @param execInfo      execution info
     * @param queryInfoList query info list
     * @since 1.4
     */
    protected void writeParamsForSingleCallableEntry(StringBuilder sb, SortedMap<String, String> paramMap, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
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
     * Enable multiline output in {@link #getLogEntry(ExecutionInfo, List, boolean, boolean)}.
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
