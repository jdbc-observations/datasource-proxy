package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.lifecycle.JdbcLifecycleEventListenerUtils;
import org.junit.jupiter.api.Test;

import java.sql.Statement;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Forces initialization of the classes holding constant collections, so a bad constant
 * (e.g. a duplicate element in {@code Set.of}) fails here instead of when the first proxy is used.
 */
class StatementMethodNamesTest {

    @Test
    void constantsLoad() throws Exception {
        List<Set<String>> sets = List.of(
                StatementMethodNames.PARAMETER_METHODS, StatementMethodNames.BATCH_PARAM_METHODS,
                StatementMethodNames.BATCH_EXEC_METHODS, StatementMethodNames.QUERY_EXEC_METHODS,
                StatementMethodNames.EXEC_METHODS, StatementMethodNames.JDBC4_METHODS,
                StatementMethodNames.METHODS_TO_RETURN_RESULTSET, StatementMethodNames.METHODS_TO_INTERCEPT,
                StatementMethodNames.METHODS_TO_OPERATE_PARAMETER, ProxyLogicSupport.COMMON_METHOD_NAMES);
        assertThat(sets).allSatisfy(set -> assertThat(set).isNotEmpty());
        assertThat(StatementMethodNames.EXEC_METHODS).contains("executeQuery", "executeBatch");
        assertThat(StatementMethodNames.METHODS_TO_INTERCEPT).contains("getConnection", "setString", "toString");

        assertThat(GeneratedKeysUtils.isMethodToRetrieveGeneratedKeys(
                Statement.class.getMethod("executeUpdate", String.class))).isTrue();
        assertThat(JdbcLifecycleEventListenerUtils.getListenerMethod("commit", true)).isNotNull();
        assertThat(JdbcLifecycleEventListenerUtils.getListenerMethod("commit", false)).isNotNull();

        // private constants: initialize the class
        Class.forName(RepeatableReadResultSetProxyLogic.class.getName(), true, getClass().getClassLoader());
    }

}
