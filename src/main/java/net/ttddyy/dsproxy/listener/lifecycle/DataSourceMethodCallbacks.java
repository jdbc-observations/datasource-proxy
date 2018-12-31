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
    // Wrapper methods
    //

    void beforeIsWrapperForOnDataSource(MethodExecutionContext executionContext);

    void beforeUnwrapOnDataSource(MethodExecutionContext executionContext);

    void afterIsWrapperForOnDataSource(MethodExecutionContext executionContext);

    void afterUnwrapOnDataSource(MethodExecutionContext executionContext);

    //
    // CommonDataSource methods
    //

    void beforeGetLoginTimeoutOnDataSource(MethodExecutionContext executionContext);

    void beforeGetLogWriterOnDataSource(MethodExecutionContext executionContext);

    void beforeGetParentLoggerOnDataSource(MethodExecutionContext executionContext);

    void beforeSetLoginTimeoutOnDataSource(MethodExecutionContext executionContext);

    void beforeSetLogWriterOnDataSource(MethodExecutionContext executionContext);

    void afterGetLoginTimeoutOnDataSource(MethodExecutionContext executionContext);

    void afterGetLogWriterOnDataSource(MethodExecutionContext executionContext);

    void afterGetParentLoggerOnDataSource(MethodExecutionContext executionContext);

    void afterSetLoginTimeoutOnDataSource(MethodExecutionContext executionContext);

    void afterSetLogWriterOnDataSource(MethodExecutionContext executionContext);


    //
    // DataSource methods
    //

    void beforeGetConnectionOnDataSource(MethodExecutionContext executionContext);

    void afterGetConnectionOnDataSource(MethodExecutionContext executionContext);


}
