package net.ttddyy.dsproxy.listener.lifecycle;

import net.ttddyy.dsproxy.listener.MethodExecutionContext;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.QueryExecutionContext;

/**
 * Callback for all JDBC proxy methods.
 *
 * This interface provides before and after method callbacks for all JDBC proxy interfaces({@link javax.sql.DataSource},
 * {@link java.sql.Connection}, {@link java.sql.Statement}, {@link java.sql.PreparedStatement},
 * {@link java.sql.CallableStatement}, {@link java.sql.ResultSet}), as well as
 * callbacks for any method calls ({@link #beforeMethod(MethodExecutionContext)}, {@link #afterMethod(MethodExecutionContext)})
 * and query executions({@link #beforeQuery(QueryExecutionContext)}, {@link #afterQuery(QueryExecutionContext)}).
 * <p>
 * Since 2.0, this interface is a child of {@link ProxyDataSourceListener}.
 * To properly invoke all the callbacks, the implementation of this class needs to be wrapped by {@link JdbcLifecycleEventExecutionListener}.
 * It automatically happens if the listener instance is registered via {@link net.ttddyy.dsproxy.support.ProxyDataSourceBuilder}.
 *
 * @author Tadaya Tsuyukubo
 * @see JdbcLifecycleEventExecutionListener
 * @see JdbcLifecycleEventListenerAdapter
 * @since 1.5
 */
public interface JdbcLifecycleEventListener extends ProxyDataSourceListener,
        WrapperMethodCallbacks, DataSourceMethodCallbacks, ConnectionMethodCallbacks,
        StatementMethodCallbacks, PreparedStatementMethodCallbacks, CallableStatementMethodCallbacks,
        ResultSetMethodCallbacks {

}
