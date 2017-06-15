package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.logging.CommonsLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;

import java.util.logging.Level;

/**
 * Builder for QueryCountLogging related classes.
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingServletFilter
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingHandlerInterceptor
 * @see net.ttddyy.dsproxy.support.JULQueryCountLoggingServletFilter
 * @see net.ttddyy.dsproxy.support.JULQueryCountLoggingHandlerInterceptor
 * @see net.ttddyy.dsproxy.support.SLF4JQueryCountLoggingServletFilter
 * @see net.ttddyy.dsproxy.support.SLF4JQueryCountLoggingHandlerInterceptor
 * @see net.ttddyy.dsproxy.support.SystemOutQueryCountLoggingServletFilter
 * @see net.ttddyy.dsproxy.support.SystemOutQueryCountLoggingHandlerInterceptor
 * @since 1.3
 */
public class QueryCountLoggerBuilder {

    private boolean writeAsJson = false;
    private CommonsLogLevel commonsLogLevel = CommonsLogLevel.DEBUG;
    private SLF4JLogLevel slf4jLogLevel = SLF4JLogLevel.DEBUG;
    private Level julLogLevel = Level.FINE;

    public static QueryCountLoggerBuilder create() {
        return new QueryCountLoggerBuilder();
    }

    /**
     * Set commons log level for query-count-logger.
     *
     * @param commonsLogLevel commons log level
     * @return builder
     */
    public QueryCountLoggerBuilder logLevel(CommonsLogLevel commonsLogLevel) {
        this.commonsLogLevel = commonsLogLevel;
        return this;
    }

    /**
     * Set slf4j log level for query-count-logger.
     *
     * @param slf4jLogLevel slf4j log level
     * @return builder
     */
    public QueryCountLoggerBuilder logLevel(SLF4JLogLevel slf4jLogLevel) {
        this.slf4jLogLevel = slf4jLogLevel;
        return this;
    }

    /**
     * Set query-count-logger format as JSON.
     *
     * @return builder
     */
    public QueryCountLoggerBuilder asJson() {
        this.writeAsJson = true;
        return this;
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.CommonsQueryCountLoggingServletFilter}.
     *
     * @return query-count-logger using commons
     */
    public CommonsQueryCountLoggingServletFilter buildCommonsFilter() {
        return buildCommonsFilter(null);
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.CommonsQueryCountLoggingServletFilter}.
     *
     * @param logLevel commons log level
     * @return query-count-logger using commons
     */
    public CommonsQueryCountLoggingServletFilter buildCommonsFilter(CommonsLogLevel logLevel) {
        if (logLevel != null) {
            this.commonsLogLevel = logLevel;
        }

        CommonsQueryCountLoggingServletFilter filter = new CommonsQueryCountLoggingServletFilter();
        if (this.commonsLogLevel != null) {
            filter.setLogLevel(this.commonsLogLevel);
        }
        filter.setWriteAsJson(this.writeAsJson);
        return filter;
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.CommonsQueryCountLoggingHandlerInterceptor}.
     *
     * @return query-count-logger using commons
     */
    public CommonsQueryCountLoggingHandlerInterceptor buildCommonsHandlerInterceptor() {
        return buildCommonsHandlerInterceptor(null);
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.CommonsQueryCountLoggingHandlerInterceptor}.
     *
     * @param logLevel commons log level
     * @return query-count-logger using commons
     */
    public CommonsQueryCountLoggingHandlerInterceptor buildCommonsHandlerInterceptor(CommonsLogLevel logLevel) {
        if (logLevel != null) {
            this.commonsLogLevel = logLevel;
        }

        CommonsQueryCountLoggingHandlerInterceptor handlerInterceptor = new CommonsQueryCountLoggingHandlerInterceptor();
        if (this.commonsLogLevel != null) {
            handlerInterceptor.setLogLevel(this.commonsLogLevel);
        }
        handlerInterceptor.setWriteAsJson(this.writeAsJson);
        return handlerInterceptor;
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.JULQueryCountLoggingServletFilter}.
     *
     * @return query-count-logger using Java Util Logging
     * @since 1.4.2
     */
    public JULQueryCountLoggingServletFilter buildJULFilter() {
        return buildJULFilter(null);
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.JULQueryCountLoggingServletFilter}.
     *
     * @param logLevel commons log level
     * @return query-count-logger using Java Util Logging
     * @since 1.4.2
     */
    public JULQueryCountLoggingServletFilter buildJULFilter(Level logLevel) {
        if (logLevel != null) {
            this.julLogLevel = logLevel;
        }

        JULQueryCountLoggingServletFilter filter = new JULQueryCountLoggingServletFilter();
        if (this.julLogLevel != null) {
            filter.setLogLevel(this.julLogLevel);
        }
        filter.setWriteAsJson(this.writeAsJson);
        return filter;
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.JULQueryCountLoggingHandlerInterceptor}.
     *
     * @return query-count-logger using Java Util Logging
     * @since 1.4.2
     */
    public JULQueryCountLoggingHandlerInterceptor buildJULHandlerInterceptor() {
        return buildJULHandlerInterceptor(null);
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.JULQueryCountLoggingHandlerInterceptor}.
     *
     * @param logLevel commons log level
     * @return query-count-logger using Java Util Logging
     * @since 1.4.2
     */
    public JULQueryCountLoggingHandlerInterceptor buildJULHandlerInterceptor(Level logLevel) {
        if (logLevel != null) {
            this.julLogLevel = logLevel;
        }

        JULQueryCountLoggingHandlerInterceptor handlerInterceptor = new JULQueryCountLoggingHandlerInterceptor();
        if (this.julLogLevel != null) {
            handlerInterceptor.setLogLevel(this.julLogLevel);
        }
        handlerInterceptor.setWriteAsJson(this.writeAsJson);
        return handlerInterceptor;
    }


    /**
     * Build {@link net.ttddyy.dsproxy.support.SLF4JQueryCountLoggingServletFilter}.
     *
     * @return query-count-logger using slf4j
     */
    public SLF4JQueryCountLoggingServletFilter buildSlf4jFilter() {
        return buildSlf4jFilter(null);
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.SLF4JQueryCountLoggingServletFilter}.
     *
     * @param logLevel slf4j log level
     * @return query-count-logger using slf4j
     */
    public SLF4JQueryCountLoggingServletFilter buildSlf4jFilter(SLF4JLogLevel logLevel) {
        if (logLevel != null) {
            this.slf4jLogLevel = logLevel;
        }

        SLF4JQueryCountLoggingServletFilter filter = new SLF4JQueryCountLoggingServletFilter();
        if (this.slf4jLogLevel != null) {
            filter.setLogLevel(this.slf4jLogLevel);
        }
        filter.setWriteAsJson(this.writeAsJson);
        return filter;
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.SLF4JQueryCountLoggingHandlerInterceptor}.
     *
     * @return query-count-logger using slf4j
     */
    public SLF4JQueryCountLoggingHandlerInterceptor buildSlf4jHandlerInterceptor() {
        return buildSlf4jHandlerInterceptor(null);
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.SLF4JQueryCountLoggingHandlerInterceptor}.
     *
     * @param logLevel slf4j log level
     * @return query-count-logger using slf4j
     */
    public SLF4JQueryCountLoggingHandlerInterceptor buildSlf4jHandlerInterceptor(SLF4JLogLevel logLevel) {
        if (logLevel != null) {
            this.slf4jLogLevel = logLevel;
        }

        SLF4JQueryCountLoggingHandlerInterceptor handlerInterceptor = new SLF4JQueryCountLoggingHandlerInterceptor();
        if (this.slf4jLogLevel != null) {
            handlerInterceptor.setLogLevel(this.slf4jLogLevel);
        }
        handlerInterceptor.setWriteAsJson(this.writeAsJson);
        return handlerInterceptor;
    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.SystemOutQueryCountLoggingServletFilter}.
     *
     * @return query-count-logger using system.out
     */
    public SystemOutQueryCountLoggingServletFilter buildSysOutFilter() {
        SystemOutQueryCountLoggingServletFilter filter = new SystemOutQueryCountLoggingServletFilter();
        filter.setWriteAsJson(this.writeAsJson);
        return filter;

    }

    /**
     * Build {@link net.ttddyy.dsproxy.support.SystemOutQueryCountLoggingHandlerInterceptor}.
     *
     * @return query-count-logger using system.out
     */
    public SystemOutQueryCountLoggingHandlerInterceptor buildSysOutHandlerInterceptor() {
        SystemOutQueryCountLoggingHandlerInterceptor handlerInterceptor = new SystemOutQueryCountLoggingHandlerInterceptor();
        handlerInterceptor.setWriteAsJson(this.writeAsJson);
        return handlerInterceptor;
    }

}
