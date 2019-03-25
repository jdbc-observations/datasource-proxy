package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;

import java.util.List;

/**
 * Callback for all JDBC proxy methods.
 *
 * This interface provides before and after method callbacks for all JDBC proxy interfaces({@link javax.sql.DataSource},
 * {@link java.sql.Connection}, {@link java.sql.Statement}, {@link java.sql.PreparedStatement},
 * {@link java.sql.CallableStatement}, {@link java.sql.ResultSet}), as well as
 * callbacks for any method calls ({@link #beforeMethod(MethodExecutionContext)}, {@link #afterMethod(MethodExecutionContext)})
 * and query executions({@link #beforeQuery(ExecutionInfo, List)}, {@link #afterQuery(ExecutionInfo, List)}).
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

    void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList);

    void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList);

}
