package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.listener.SLF4JLogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.util.Collections;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryCountLoggingRequestListener implements ServletRequestListener {

    private static final String LOG_LEVEL_PARAM = "queryCountSLF4JLogLevel";
    private static final SLF4JLogLevel DEFAULT_LOG_LEVEL = SLF4JLogLevel.DEBUG;

    private static final String LOG_MESSAGE =
            "DataSource:{} ElapsedTime:{} Call:{} Query:{} (Select:{} Insert:{} Update:{} Delete:{} Other{})";

    private Logger logger = LoggerFactory.getLogger(SLF4JQueryCountLoggingRequestListener.class);


    public void requestInitialized(ServletRequestEvent sre) {
    }

    public void requestDestroyed(ServletRequestEvent sre) {

        final ServletContext context = sre.getServletContext();
        final String logLevelParam = context.getInitParameter(LOG_LEVEL_PARAM);
        SLF4JLogLevel logLevel = SLF4JLogLevel.nullSafeValueOf(logLevelParam);
        if (logLevel == null) {
            logLevel = DEFAULT_LOG_LEVEL;
        }

        final List<String> dsNames = QueryCountHolder.getDataSourceNamesAsList();
        Collections.sort(dsNames);

        for (String dsName : dsNames) {
            final QueryCount counter = QueryCountHolder.get(dsName);
            final Object[] args = {dsName, counter.getElapsedTime(), counter.getCall(), counter.getTotalNumOfQuery(),
                    counter.getSelect(), counter.getInsert(), counter.getUpdate(), counter.getDelete(), counter.getOther()};
            writeLog(logLevel, args);
        }

        QueryCountHolder.clear();

    }

    private void writeLog(SLF4JLogLevel logLevel, Object[] argArray) {
        switch (logLevel) {
            case DEBUG:
                logger.debug(LOG_MESSAGE, argArray);
                break;
            case ERROR:
                logger.error(LOG_MESSAGE, argArray);
                break;
            case INFO:
                logger.info(LOG_MESSAGE, argArray);
                break;
            case TRACE:
                logger.trace(LOG_MESSAGE, argArray);
                break;
            case WARN:
                logger.warn(LOG_MESSAGE, argArray);
                break;
        }
    }


}
