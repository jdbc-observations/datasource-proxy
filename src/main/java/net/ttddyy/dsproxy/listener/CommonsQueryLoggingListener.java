package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Record executed query information using Commons-Logging.
 *
 * @author Tadaya Tsuyukubo
 */
public class CommonsQueryLoggingListener implements QueryExecutionListener {

    private boolean writeDataSourceName = true;

    private Log log = LogFactory.getLog(CommonsQueryLoggingListener.class);
    private CommonsLogLevel logLevel = CommonsLogLevel.DEBUG; // default DEBUG

    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    }

    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        final StringBuilder sb = new StringBuilder();

        if (writeDataSourceName) {
            sb.append("Name:");
            sb.append(execInfo.getDataSourceName());
            sb.append(", ");
        }

        sb.append("Time:");
        sb.append(execInfo.getElapsedTime());
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
            chopIfEndWith(sb, ',');

            sb.append("]");
            sb.append("} ");
        }

        writeLog(sb.toString());
    }

    private void chopIfEndWith(StringBuilder sb, char c) {
        final int lastCharIndex = sb.length() - 1;
        if (sb.charAt(lastCharIndex) == c) {
            sb.deleteCharAt(lastCharIndex);
        }
    }

    private void writeLog(String message) {
        switch (logLevel) {
            case DEBUG:
                log.debug(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case FATAL:
                log.fatal(message);
                break;
            case INFO:
                log.info(message);
                break;
            case TRACE:
                log.trace(message);
                break;
            case WARN:
                log.warn(message);
                break;
        }
    }

    public void setLogLevel(CommonsLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void setWriteDataSourceName(boolean writeDataSourceName) {
        this.writeDataSourceName = writeDataSourceName;
    }
}
