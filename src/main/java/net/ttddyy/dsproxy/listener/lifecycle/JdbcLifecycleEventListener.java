package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;

/**
 * Callback for all JDBC proxy methods.
 *
 * This interface provides before and after method callbacks for all JDBC proxy interfaces({@link javax.sql.DataSource},
 * {@link java.sql.Connection}, {@link java.sql.Statement}, {@link java.sql.PreparedStatement},
 * {@link java.sql.CallableStatement}, {@link java.sql.ResultSet}), as well as
 * callbacks for any method calls ({@link #beforeMethod(MethodExecutionContext)}, {@link #afterMethod(MethodExecutionContext)})
 * and query executions({@link #beforeQuery(QueryExecutionContext)}, {@link #afterQuery(QueryExecutionContext)}).
 *
 * @author Tadaya Tsuyukubo
 * @see JdbcLifecycleEventExecutionListener
 * @see JdbcLifecycleEventListenerAdapter
 * @since 1.5
 */
public interface JdbcLifecycleEventListener extends WrapperMethodCallbacks, DataSourceMethodCallbacks, ConnectionMethodCallbacks,
        StatementMethodCallbacks, PreparedStatementMethodCallbacks, CallableStatementMethodCallbacks,
        ResultSetMethodCallbacks {

    void beforeMethod(MethodExecutionContext executionContext);

    void afterMethod(MethodExecutionContext executionContext);

    void beforeQuery(QueryExecutionContext executionContext);

    void afterQuery(QueryExecutionContext executionContext);

}
