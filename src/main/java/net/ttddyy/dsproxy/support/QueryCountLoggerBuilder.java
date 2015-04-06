package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.CommonsLogLevel;
import net.ttddyy.dsproxy.listener.SLF4JLogLevel;

/**
 * Builder for QueryCountLogging related classes.
 *
 * @author Tadaya Tsuyukubo
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingFilter
 * @see net.ttddyy.dsproxy.support.CommonsQueryCountLoggingHandlerInterceptor
 * @see net.ttddyy.dsproxy.support.SLF4JQueryCountLoggingFilter
 * @see net.ttddyy.dsproxy.support.SLF4JQueryCountLoggingHandlerInterceptor
 * @since 1.3
 */
public class QueryCountLoggerBuilder {

    private boolean writeAsJson = false;
    private CommonsLogLevel commonsLogLevel = CommonsLogLevel.DEBUG;
    private SLF4JLogLevel slf4jLogLevel = SLF4JLogLevel.DEBUG;

    public static QueryCountLoggerBuilder create() {
        return new QueryCountLoggerBuilder();
    }

    public QueryCountLoggerBuilder logLevel(CommonsLogLevel commonsLogLevel) {
        this.commonsLogLevel = commonsLogLevel;
        return this;
    }

    public QueryCountLoggerBuilder logLevel(SLF4JLogLevel slf4jLogLevel) {
        this.slf4jLogLevel = slf4jLogLevel;
        return this;
    }

    public QueryCountLoggerBuilder asJson() {
        this.writeAsJson = true;
        return this;
    }

    public CommonsQueryCountLoggingFilter buildCommonsFilter() {
        return buildCommonsFilter(null);
    }

    public CommonsQueryCountLoggingFilter buildCommonsFilter(CommonsLogLevel logLevel) {
        if (logLevel != null) {
            this.commonsLogLevel = logLevel;
        }

        CommonsQueryCountLoggingFilter filter = new CommonsQueryCountLoggingFilter();
        if (this.commonsLogLevel != null) {
            filter.setLogLevel(this.commonsLogLevel);
        }
        filter.setWriteAsJson(this.writeAsJson);
        return filter;
    }

    public CommonsQueryCountLoggingHandlerInterceptor buildCommonsHandlerInterceptor() {
        return buildCommonsHandlerInterceptor(null);
    }

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


    public SLF4JQueryCountLoggingFilter buildSlf4jFilter() {
        return buildSlf4jFilter(null);
    }

    public SLF4JQueryCountLoggingFilter buildSlf4jFilter(SLF4JLogLevel logLevel) {
        if (logLevel != null) {
            this.slf4jLogLevel = logLevel;
        }

        SLF4JQueryCountLoggingFilter filter = new SLF4JQueryCountLoggingFilter();
        if (this.slf4jLogLevel != null) {
            filter.setLogLevel(this.slf4jLogLevel);
        }
        return filter;
    }

    public SLF4JQueryCountLoggingHandlerInterceptor buildSlf4jHandlerInterceptor() {
        return buildSlf4jHandlerInterceptor(null);
    }

    public SLF4JQueryCountLoggingHandlerInterceptor buildSlf4jHandlerInterceptor(SLF4JLogLevel logLevel) {
        if (logLevel != null) {
            this.slf4jLogLevel = logLevel;
        }

        SLF4JQueryCountLoggingHandlerInterceptor handlerInterceptor = new SLF4JQueryCountLoggingHandlerInterceptor();
        if (this.slf4jLogLevel != null) {
            handlerInterceptor.setLogLevel(this.slf4jLogLevel);
        }
        return handlerInterceptor;
    }

}
