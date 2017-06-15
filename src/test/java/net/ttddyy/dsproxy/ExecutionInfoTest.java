package net.ttddyy.dsproxy;

import org.junit.Test;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.3.1
 */
public class ExecutionInfoTest {

    @Test
    public void statementType() {
        Statement statement = mock(Statement.class);
        PreparedStatement prepared = mock(PreparedStatement.class);
        CallableStatement callable = mock(CallableStatement.class);

        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.setDataSourceName("");

        ExecutionInfo executionInfo;
        executionInfo = new ExecutionInfo(connectionInfo, statement, true, 0, null, null);
        assertThat(executionInfo.getStatementType()).isEqualTo(StatementType.STATEMENT);

        executionInfo = new ExecutionInfo(connectionInfo, prepared, true, 0, null, null);
        assertThat(executionInfo.getStatementType()).isEqualTo(StatementType.PREPARED);

        executionInfo = new ExecutionInfo(connectionInfo, callable, true, 0, null, null);
        assertThat(executionInfo.getStatementType()).isEqualTo(StatementType.CALLABLE);

    }
}
