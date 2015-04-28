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
public class StatementTypeTest {

    @Test
    public void valueOf() {
        Statement statement = mock(Statement.class);
        PreparedStatement prepared = mock(PreparedStatement.class);
        CallableStatement callable = mock(CallableStatement.class);

        assertThat(StatementType.valueOf(statement)).isEqualTo(StatementType.STATEMENT);
        assertThat(StatementType.valueOf(prepared)).isEqualTo(StatementType.PREPARED);
        assertThat(StatementType.valueOf(callable)).isEqualTo(StatementType.CALLABLE);
    }
}
