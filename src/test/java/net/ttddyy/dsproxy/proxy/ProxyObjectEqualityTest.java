package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.proxy.jdk.JdkJdbcProxyFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test the equality of proxy objects.
 *
 * @author Tadaya Tsuyukubo
 */
public class ProxyObjectEqualityTest {

    private static JdkJdbcProxyFactory proxyFactory = new JdkJdbcProxyFactory();


    public static List<Object[]> inputs() {
        return Arrays.asList(
                dataSourceData(),
                connectionData(),
                statementData(),
                preparedData(),
                callableData(),
                resultSetData()
        );
    }

    private static Object[] dataSourceData() {
        DataSource ds1 = mock(DataSource.class);
        DataSource ds2 = mock(DataSource.class);
        assertThat(ds1).isNotEqualTo(ds2);

        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();
        DataSource proxy1 = proxyFactory.createDataSource(ds1, proxyConfig);
        DataSource proxy2 = proxyFactory.createDataSource(ds1, proxyConfig);
        DataSource proxy3 = proxyFactory.createDataSource(ds2, proxyConfig);

        return new Object[]{"DataSource", proxy1, proxy2, proxy3, ds1, ds2};
    }

    private static Object[] connectionData() {
        Connection conn1 = mock(Connection.class);
        Connection conn2 = mock(Connection.class);
        assertThat(conn1).isNotEqualTo(conn2);

        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();
        Connection proxy1 = proxyFactory.createConnection(conn1, null, proxyConfig);
        Connection proxy2 = proxyFactory.createConnection(conn1, null, proxyConfig);
        Connection proxy3 = proxyFactory.createConnection(conn2, null, proxyConfig);

        return new Object[]{"Connection", proxy1, proxy2, proxy3, conn1, conn2};
    }

    private static Object[] statementData() {
        Statement stmt1 = mock(Statement.class);
        Statement stmt2 = mock(Statement.class);
        assertThat(stmt1).isNotEqualTo(stmt2);

        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();
        Statement proxy1 = proxyFactory.createStatement(stmt1, null, null, proxyConfig);
        Statement proxy2 = proxyFactory.createStatement(stmt1, null, null, proxyConfig);
        Statement proxy3 = proxyFactory.createStatement(stmt2, null, null, proxyConfig);

        return new Object[]{"Statement", proxy1, proxy2, proxy3, stmt1, stmt2};
    }

    private static Object[] preparedData() {
        PreparedStatement stmt1 = mock(PreparedStatement.class);
        PreparedStatement stmt2 = mock(PreparedStatement.class);
        assertThat(stmt1).isNotEqualTo(stmt2);

        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();
        PreparedStatement proxy1 = proxyFactory.createPreparedStatement(stmt1, "query", null, null, proxyConfig, true);
        PreparedStatement proxy2 = proxyFactory.createPreparedStatement(stmt1, "query", null, null, proxyConfig, true);
        PreparedStatement proxy3 = proxyFactory.createPreparedStatement(stmt2, "query", null, null, proxyConfig, true);

        return new Object[]{"Prepared", proxy1, proxy2, proxy3, stmt1, stmt2};
    }

    private static Object[] callableData() {
        CallableStatement stmt1 = mock(CallableStatement.class);
        CallableStatement stmt2 = mock(CallableStatement.class);
        assertThat(stmt1).isNotEqualTo(stmt2);

        ProxyConfig proxyConfig = ProxyConfig.Builder.create().build();
        CallableStatement proxy1 = proxyFactory.createCallableStatement(stmt1, "query", null, null, proxyConfig);
        CallableStatement proxy2 = proxyFactory.createCallableStatement(stmt1, "query", null, null, proxyConfig);
        CallableStatement proxy3 = proxyFactory.createCallableStatement(stmt2, "query", null, null, proxyConfig);

        return new Object[]{"Callable", proxy1, proxy2, proxy3, stmt1, stmt2};
    }

    private static Object[] resultSetData() {
        ResultSet rs1 = mock(ResultSet.class);
        ResultSet rs2 = mock(ResultSet.class);
        assertThat(rs1).isNotEqualTo(rs2);

        ProxyConfig proxyConfig = ProxyConfig.Builder.create().resultSetProxyLogicFactory(ResultSetProxyLogicFactory.DEFAULT).build();
        ResultSet proxy1 = proxyFactory.createResultSet(rs1, null, proxyConfig);
        ResultSet proxy2 = proxyFactory.createResultSet(rs1, null, proxyConfig);
        ResultSet proxy3 = proxyFactory.createResultSet(rs2, null, proxyConfig);

        return new Object[]{"ResultSet", proxy1, proxy2, proxy3, rs1, rs2};
    }

    @MethodSource("inputs")
    // proxies of Connection/Statement/ResultSet are AutoCloseable; do not let JUnit close them
    @ParameterizedTest(name = "{0}", autoCloseArguments = false)
    void equality(String name, Object proxy, Object proxyWithSame, Object proxyWithDifferent, Object original, Object different) {
        assertThat(proxy)
                .isEqualTo(proxy)
                .isEqualTo(proxyWithSame)
                .isNotEqualTo(proxyWithDifferent)
                .isEqualTo(original)
                .isNotEqualTo(different);
    }

}
