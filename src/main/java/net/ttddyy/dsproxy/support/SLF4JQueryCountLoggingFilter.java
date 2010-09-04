package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;
import net.ttddyy.dsproxy.listener.SLF4JLogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryCountLoggingFilter implements Filter {

    private static final String CLEAR_QUERY_COUNTER_PARAM = "clearQueryCounter";
    private static final String LOG_LEVEL_PARAM = "logLevel";

    private static final String LOG_MESSAGE =
            "DataSource:{} ElapsedTime:{} Call:{} Query:{} (Select:{} Insert:{} Update:{} Delete:{} Other{})";

    private Logger logger = LoggerFactory.getLogger(SLF4JQueryCountLoggingFilter.class);

    private boolean clearQueryCounter = true;
    private SLF4JLogLevel logLevel = SLF4JLogLevel.DEBUG;


    public void init(FilterConfig filterConfig) throws ServletException {
        final String clearQueryCounterParam = filterConfig.getInitParameter(CLEAR_QUERY_COUNTER_PARAM);
        if (clearQueryCounterParam != null && "false".equalsIgnoreCase(clearQueryCounterParam)) {
            this.clearQueryCounter = false;
        }

        final String logLevelParam = filterConfig.getInitParameter(LOG_LEVEL_PARAM);
        final SLF4JLogLevel logLevel = SLF4JLogLevel.nullSafeValueOf(logLevelParam);
        if (logLevel != null) {
            this.logLevel = logLevel;
        }

    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);

        final List<String> dsNames = QueryCountHolder.getDataSourceNamesAsList();
        Collections.sort(dsNames);

        for (String dsName : dsNames) {
            final QueryCount counter = QueryCountHolder.get(dsName);
            Object[] args = {dsName, counter.getElapsedTime(), counter.getCall(), counter.getTotalNumOfQuery(),
                    counter.getSelect(), counter.getInsert(), counter.getUpdate(), counter.getDelete(), counter.getOther()};
            writeLog(args);
        }

        if (clearQueryCounter) {
            QueryCountHolder.clear();
        }

    }

    private void writeLog(Object[] argArray) {
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
            case TRAC:
                logger.trace(LOG_MESSAGE, argArray);
                break;
            case WARN:
                logger.warn(LOG_MESSAGE, argArray);
                break;
        }
    }


    public void setClearQueryCounter(boolean clearQueryCounter) {
        this.clearQueryCounter = clearQueryCounter;
    }

    public void setLogLevel(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }
}
