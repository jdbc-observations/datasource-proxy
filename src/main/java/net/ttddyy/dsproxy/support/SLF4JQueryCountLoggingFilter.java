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

    private Logger logger = LoggerFactory.getLogger(SLF4JQueryCountLoggingFilter.class);

    private boolean clearQueryCounter = true;
    private SLF4JLogLevel logLevel = SLF4JLogLevel.DEBUG;
    private QueryCountLogFormatter logFormatter = new DefaultQueryCountLogFormatter();

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
            final String message = logFormatter.getLogMessage(dsName, counter);
            SLF4JLogUtils.writeLog(logger, logLevel, message);
        }

        if (clearQueryCounter) {
            QueryCountHolder.clear();
        }

    }


    public void setClearQueryCounter(boolean clearQueryCounter) {
        this.clearQueryCounter = clearQueryCounter;
    }

    public void setLogLevel(SLF4JLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void setLogFormatter(QueryCountLogFormatter logFormatter) {
        this.logFormatter = logFormatter;
    }
}
