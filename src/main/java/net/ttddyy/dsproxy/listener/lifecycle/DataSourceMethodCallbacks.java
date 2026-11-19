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

    default void beforeGetLoginTimeout(MethodExecutionContext executionContext) {

    }

    default void beforeGetLogWriter(MethodExecutionContext executionContext) {

    }

    default void beforeGetParentLogger(MethodExecutionContext executionContext) {

    }

    default void beforeSetLoginTimeout(MethodExecutionContext executionContext) {

    }

    default void beforeSetLogWriter(MethodExecutionContext executionContext) {

    }

    default void afterGetLoginTimeout(MethodExecutionContext executionContext) {

    }

    default void afterGetLogWriter(MethodExecutionContext executionContext) {

    }

    default void afterGetParentLogger(MethodExecutionContext executionContext) {

    }

    default void afterSetLoginTimeout(MethodExecutionContext executionContext) {

    }

    default void afterSetLogWriter(MethodExecutionContext executionContext) {

    }


    //
    // DataSource methods
    //

    default void beforeGetConnection(MethodExecutionContext executionContext) {

    }

    default void beforeCreateConnectionBuilder(MethodExecutionContext executionContext) {

    }

    default void beforeCreateShardingKeyBuilder(MethodExecutionContext executionContext) {

    }

    default void afterGetConnection(MethodExecutionContext executionContext) {

    }

    default void afterCreateConnectionBuilder(MethodExecutionContext executionContext) {

    }

    default void afterCreateShardingKeyBuilder(MethodExecutionContext executionContext) {

    }


}
