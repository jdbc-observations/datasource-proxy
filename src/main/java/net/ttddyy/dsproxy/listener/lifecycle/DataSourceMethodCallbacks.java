package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;

/**
 * Defines callback methods for {@link javax.sql.DataSource}.
 *
 * @author Tadaya Tsuyukubo
 * @see javax.sql.DataSource
 * @see javax.sql.CommonDataSource
 * @since 1.5
 */
public interface DataSourceMethodCallbacks {

    //
    // CommonDataSource methods
    //

    void beforeGetLoginTimeout(MethodExecutionContext executionContext);

    void beforeGetLogWriter(MethodExecutionContext executionContext);

    void beforeGetParentLogger(MethodExecutionContext executionContext);

    void beforeSetLoginTimeout(MethodExecutionContext executionContext);

    void beforeSetLogWriter(MethodExecutionContext executionContext);

    void afterGetLoginTimeout(MethodExecutionContext executionContext);

    void afterGetLogWriter(MethodExecutionContext executionContext);

    void afterGetParentLogger(MethodExecutionContext executionContext);

    void afterSetLoginTimeout(MethodExecutionContext executionContext);

    void afterSetLogWriter(MethodExecutionContext executionContext);


    //
    // DataSource methods
    //

    void beforeGetConnection(MethodExecutionContext executionContext);

    void afterGetConnection(MethodExecutionContext executionContext);


}
