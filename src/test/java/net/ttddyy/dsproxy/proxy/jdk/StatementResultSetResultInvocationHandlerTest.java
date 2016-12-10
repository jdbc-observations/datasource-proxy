package net.ttddyy.dsproxy.proxy.jdk;

import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static net.ttddyy.dsproxy.proxy.jdk.StatementResultSetResultInvocationHandler.statementResultSetResultProxy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Liam Williams
 */
public class StatementResultSetResultInvocationHandlerTest {

    @Test
    public void nonResultSetResultMethodsPassThrough() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        PreparedStatement proxy = statementResultSetResultProxy(statement, PreparedStatement.class);

        proxy.executeUpdate();

        verify(statement).executeUpdate();
    }
}