package net.ttddyy.dsproxy;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import org.junit.jupiter.api.Test;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3.1
 */
public class QueryExecutionContextTest {

    @Test
    public void statementType() {
        Statement statement = mock(Statement.class);
        PreparedStatement prepared = mock(PreparedStatement.class);
        CallableStatement callable = mock(CallableStatement.class);

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("");

        QueryExecutionContext queryExecutionContext;
        queryExecutionContext = new QueryExecutionContext(connectionInfo, statement, true, 0, null, null, null);
        assertThat(queryExecutionContext.getStatementType()).isEqualTo(StatementType.STATEMENT);

        queryExecutionContext = new QueryExecutionContext(connectionInfo, prepared, true, 0, null, null, null);
        assertThat(queryExecutionContext.getStatementType()).isEqualTo(StatementType.PREPARED);

        queryExecutionContext = new QueryExecutionContext(connectionInfo, callable, true, 0, null, null, null);
        assertThat(queryExecutionContext.getStatementType()).isEqualTo(StatementType.CALLABLE);

    }
}
