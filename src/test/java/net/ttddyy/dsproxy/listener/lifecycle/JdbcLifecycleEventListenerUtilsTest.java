package net.ttddyy.dsproxy.listener.lifecycle;

import org.junit.jupiter.api.Test;

import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Wrapper;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
class JdbcLifecycleEventListenerUtilsTest {

    // Class Hierarchy:
    //   DataSource : DataSource & CommonDataSource
    //   Statement, PreparedStatement, CallableStatement
    //   Connection, ResultSet
    //   Wrapper : everyone inherits Wrapper

    // TODO: make parameterized tests
    private List<Class<? extends Wrapper>> proxyClasses = Arrays.asList(DataSource.class, Connection.class, ResultSet.class,
            Statement.class, PreparedStatement.class, CallableStatement.class);


    @Test
    void getListenerMethod() {

        for (Class<? extends Wrapper> proxyClass : this.proxyClasses) {
            Method[] methods = proxyClass.getMethods();  // get methods including ones declared on parents such as Wrapper, CommonDataSource

            for (Method method : methods) {
                String methodName = method.getName();

                String expectedBeforeName = "before" + capitalize(methodName);
                String expectedAfterName = "after" + capitalize(methodName);

                Method resolvedBeforeMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(methodName, true);
                Method resolvedAfterMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(methodName, false);

                String messageForBefore = format("Failed for %s on %s. expected=%s", methodName, proxyClass.getSimpleName(), expectedBeforeName);
                String messageForAfter = format("Failed for %s on %s. expected=%s", methodName, proxyClass.getSimpleName(), expectedAfterName);
                assertThat(resolvedBeforeMethod).as(messageForBefore).isNotNull();
                assertThat(resolvedAfterMethod).as(messageForAfter).isNotNull();
                assertThat(resolvedBeforeMethod.getName()).isEqualTo(expectedBeforeName);
                assertThat(resolvedAfterMethod.getName()).isEqualTo(expectedAfterName);
            }

        }

    }

    @Test
    void getListenerMethodWithObjectMethods() {

        Method[] methods = Object.class.getMethods();
        for (Class<? extends Wrapper> proxyClass : this.proxyClasses) {

            for (Method method : methods) {
                String methodName = method.getName();
                Method resolvedBeforeMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(methodName, true);
                Method resolvedAfterMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(methodName, false);

                String message = format("method=%s, proxyClass=%s", method.getName(), proxyClass.getSimpleName());
                assertThat(resolvedBeforeMethod).as(message).isNull();
                assertThat(resolvedAfterMethod).as(message).isNull();
            }

        }

    }

    @Test
    void getListenerMethodWithWrapper() {

        // explicitly test for methods on Wrapper
        for (Method method : Wrapper.class.getMethods()) {
            String methodName = method.getName();
            String expectedBeforeName = "before" + capitalize(methodName);
            String expectedAfterName = "after" + capitalize(methodName);
            Method resolvedBeforeMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(methodName, true);
            Method resolvedAfterMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(methodName, false);

            assertThat(resolvedBeforeMethod.getName()).isEqualTo(expectedBeforeName);
            assertThat(resolvedAfterMethod.getName()).isEqualTo(expectedAfterName);
        }
    }

    @Test
    void getListenerMethodWithCommonDataSource() {

        for (Method method : CommonDataSource.class.getMethods()) {
            String methodName = method.getName();
            String expectedBeforeName = "before" + capitalize(methodName);
            String expectedAfterName = "after" + capitalize(methodName);
            Method resolvedBeforeMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(methodName, true);
            Method resolvedAfterMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(methodName, false);

            assertThat(resolvedBeforeMethod.getName()).isEqualTo(expectedBeforeName);
            assertThat(resolvedAfterMethod.getName()).isEqualTo(expectedAfterName);
        }
    }


    private String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}
