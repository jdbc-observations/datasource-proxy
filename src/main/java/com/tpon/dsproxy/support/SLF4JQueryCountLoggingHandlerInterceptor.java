package com.tpon.dsproxy.support;

import com.tpon.dsproxy.listener.CommonsLogLevel;
import com.tpon.dsproxy.listener.SLF4JLogLevel;
import com.tpon.dsproxy.QueryCountHolder;
import com.tpon.dsproxy.QueryCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Collections;

/**
 * @author Tadaya Tsuyukubo
 */
public class SLF4JQueryCountLoggingHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final String LOG_MESSAGE =
            "DataSource:{} ElapsedTime:{} Call:{} Query:{} (Select:{} Insert:{} Update:{} Delete:{} Other{})";
    
    private Logger logger = LoggerFactory.getLogger(SLF4JQueryCountLoggingHandlerInterceptor.class);

    private boolean clearQueryCounter = true;
    private SLF4JLogLevel logLevel = SLF4JLogLevel.DEBUG;


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        final List<String> dsNames = QueryCountHolder.getDataSourceNamesAsList();
        Collections.sort(dsNames);

        for (String dsName : dsNames) {
            final QueryCount counter = QueryCountHolder.get(dsName);
            final Object[] args = {dsName, counter.getElapsedTime(), counter.getCall(), counter.getTotalNumOfQuery(),
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
