package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Log executed query information using SLF4J.
 *
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryLoggingListener implements QueryExecutionListener {

    private static final String MESSAGE = "Name:{} Time:{} Success:{} Num:{} Query:{}";

    private Logger logger = LoggerFactory.getLogger(SLF4JQueryLoggingListener.class);
    private SLF4JLogLevel logLevel = SLF4JLogLevel.DEBUG; // default DEBUG

    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
    }

    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        final String dataSourceName = execInfo.getDataSourceName();
        final long elapsedTime = execInfo.getElapsedTime();
        final int numOfQuery = queryInfoList.size();
        final boolean isSuccess = execInfo.getThrowable() == null;

        final StringBuilder sb = new StringBuilder();
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
        final String queries = sb.toString();

        writeLog(new Object[]{dataSourceName, elapsedTime, isSuccess, numOfQuery, queries});

    }

    private void chopIfEndWith(StringBuilder sb, char c) {
        final int lastCharIndex = sb.length() - 1;
        if (sb.charAt(lastCharIndex) == c) {
            sb.deleteCharAt(lastCharIndex);
        }
    }


    private void writeLog(Object[] argArray) {
        switch (logLevel) {
            case DEBUG:
                logger.debug(MESSAGE, argArray);
                break;
            case ERROR:
                logger.error(MESSAGE, argArray);
                break;
            case INFO:
                logger.info(MESSAGE, argArray);
                break;
            case TRACE:
                logger.trace(MESSAGE, argArray);
                break;
            case WARN:
                logger.warn(MESSAGE, argArray);
                break;
        }
    }

    public void setLogLevel(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }
}
