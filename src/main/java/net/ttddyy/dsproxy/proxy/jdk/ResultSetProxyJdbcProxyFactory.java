package net.ttddyy.dsproxy.proxy.jdk;

import net.ttddyy.dsproxy.proxy.InterceptorHolder;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static net.ttddyy.dsproxy.proxy.jdk.StatementResultSetResultInvocationHandler.statementResultSetResultProxy;

/**
 * Extension of {@link net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory} that also proxies any
 * {@link java.sql.ResultSet} results so that they can be consumed more than once.
 *
 * @author Liam Williams
 */
public class ResultSetProxyJdbcProxyFactory extends JdkJdbcProxyFactory {

    @Override
    public Statement createStatement(Statement statement, InterceptorHolder interceptorHolder, String dataSourceName) {
        return super.createStatement(statementResultSetResultProxy(statement, Statement.class), interceptorHolder, dataSourceName);
    }

    @Override
    public PreparedStatement createPreparedStatement(PreparedStatement preparedStatement, String query, InterceptorHolder interceptorHolder, String dataSourceName) {
        return super.createPreparedStatement(statementResultSetResultProxy(preparedStatement, PreparedStatement.class), query, interceptorHolder, dataSourceName);
    }

    @Override
    public CallableStatement createCallableStatement(CallableStatement callableStatement, String query, InterceptorHolder interceptorHolder, String dataSourceName) {
        return super.createCallableStatement(statementResultSetResultProxy(callableStatement, CallableStatement.class), query, interceptorHolder, dataSourceName);
    }
}
