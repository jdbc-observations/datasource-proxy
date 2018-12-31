package net.ttddyy.dsproxy.listener.lifecycle;

import org.junit.Test;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class JdbcLifecycleEventListenerUtilsTest {

    // Class Hierarchy:
    //   DataSource : DataSource & CommonDataSource
    //   Statement, PreparedStatement, CallableStatement
    //   Connection, ResultSet
    //   Wrapper : everyone inherits Wrapper

    // TODO: make parameterized tests
    private List<Class<? extends Wrapper>> proxyClasses = Arrays.asList(DataSource.class, Connection.class, ResultSet.class,
            Statement.class, PreparedStatement.class, CallableStatement.class);


    @Test
    public void getTargetMethodName() {

        for (Class<? extends Wrapper> proxyClass : this.proxyClasses) {
            Method[] methods = proxyClass.getMethods();  // get methods including ones declared on parents

            // for Statement, PreparedStatement, and CallableStatement have inheritance relationship,
            // callback methods are based on declared class.
            if (Statement.class.isAssignableFrom(proxyClass)) {
                methods = proxyClass.getDeclaredMethods();  // get methods only declared on the class
            }

            String className = proxyClass.getSimpleName();

            for (Method method : methods) {
                String methodName = capitalize(method.getName());
                String expectedBeforeName = "before" + methodName + "On" + className;  // beforeXxxOnYyy
                String expectedAfterName = "after" + methodName + "On" + className;    // afterXxxOnYyy
                String resolvedBeforeName = JdbcLifecycleEventListenerUtils.getTargetMethodName(method, proxyClass, true);
                String resolvedAfterName = JdbcLifecycleEventListenerUtils.getTargetMethodName(method, proxyClass, false);

                assertEquals(expectedBeforeName, resolvedBeforeName);
                assertEquals(expectedAfterName, resolvedAfterName);
            }

        }

    }

    @Test
    public void getTargetMethodNameWithWrapperMethods() {

        // Corresponding lifecycle methods for Wrapper are defined based on target class.
        // e.g.: beforeUnwrapOnStatement, afterIsWrapperForResultSet

        for (Class<? extends Wrapper> proxyClass : this.proxyClasses) {

            String className = proxyClass.getSimpleName();

            for (Method method : Wrapper.class.getMethods()) {
                String methodName = capitalize(method.getName());
                String expectedBeforeName = "before" + methodName + "On" + className;  // beforeXxxOnYyy
                String expectedAfterName = "after" + methodName + "On" + className;    // afterXxxOnYyy
                String resolvedBeforeName = JdbcLifecycleEventListenerUtils.getTargetMethodName(method, proxyClass, true);
                String resolvedAfterName = JdbcLifecycleEventListenerUtils.getTargetMethodName(method, proxyClass, false);

                assertEquals(expectedBeforeName, resolvedBeforeName);
                assertEquals(expectedAfterName, resolvedAfterName);
            }
        }

    }

    @Test
    public void getListenerMethod() {

        for (Class<? extends Wrapper> proxyClass : this.proxyClasses) {
            Method[] methods = proxyClass.getMethods();  // get methods including ones declared on parents such as Wrapper, CommonDataSource

            // for Statement, PreparedStatement, and CallableStatement have inheritance relationship,
            // callback methods are based on declared class.
            if (Statement.class.isAssignableFrom(proxyClass)) {
                methods = proxyClass.getDeclaredMethods();  // get methods only declared on the class
            }


            Wrapper mock = mock(proxyClass);

            for (Method method : methods) {
                String beforeMethodName = JdbcLifecycleEventListenerUtils.getTargetMethodName(method, proxyClass, true);
                String afterMethodName = JdbcLifecycleEventListenerUtils.getTargetMethodName(method, proxyClass, false);

                Method resolvedBeforeMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(method, mock, true);
                Method resolvedAfterMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(method, mock, false);

                String messageForBefore = format("Failed for %s on %s. expected=%s", method.getName(), proxyClass.getSimpleName(), beforeMethodName);
                String messageForAfter = format("Failed for %s on %s. expected=%s", method.getName(), proxyClass.getSimpleName(), afterMethodName);
                assertNotNull(messageForBefore, resolvedBeforeMethod);
                assertNotNull(messageForAfter, resolvedAfterMethod);
                assertEquals(beforeMethodName, resolvedBeforeMethod.getName());
                assertEquals(afterMethodName, resolvedAfterMethod.getName());
            }

        }

    }

    @Test
    public void getListenerMethodWithObjectMethods() {

        Method[] methods = Object.class.getMethods();
        for (Class<? extends Wrapper> proxyClass : this.proxyClasses) {
            Wrapper mock = mock(proxyClass);

            for (Method method : methods) {
                Method resolvedBeforeMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(method, mock, true);
                Method resolvedAfterMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(method, mock, false);

                String message = format("method=%s, proxyClass=%s", method.getName(), proxyClass.getSimpleName());
                assertNull(message, resolvedBeforeMethod);
                assertNull(message, resolvedAfterMethod);
            }

        }

    }

    @Test
    public void getListenerMethodWithWrapper() {

        Method[] methods = Wrapper.class.getMethods();
        for (Class<? extends Wrapper> proxyClass : this.proxyClasses) {
            Wrapper mock = mock(proxyClass);
            String className = proxyClass.getSimpleName();

            for (Method method : methods) {
                Method resolvedBeforeMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(method, mock, true);
                Method resolvedAfterMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(method, mock, false);

                String methodName = method.getName();
                String expectedBeforeMethodName = "before" + capitalize(methodName) + "On" + className;
                String expectedAfterMethodName = "after" + capitalize(methodName) + "On" + className;

                String message = "Expected method=";
                assertNotNull(message + expectedBeforeMethodName, resolvedBeforeMethod);
                assertNotNull(message + expectedAfterMethodName, resolvedAfterMethod);
                assertEquals(expectedBeforeMethodName, resolvedBeforeMethod.getName());
                assertEquals(expectedAfterMethodName, resolvedAfterMethod.getName());
            }

        }
    }

    @Test
    public void getListenerMethodWithCommonDataSource() {

        DataSource mock = mock(DataSource.class);

        for (Method method : CommonDataSource.class.getMethods()) {
            Method resolvedBeforeMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(method, mock, true);
            Method resolvedAfterMethod = JdbcLifecycleEventListenerUtils.getListenerMethod(method, mock, false);

            String methodName = method.getName();
            String expectedBeforeMethodName = "before" + capitalize(methodName) + "OnDataSource";
            String expectedAfterMethodName = "after" + capitalize(methodName) + "OnDataSource";

            assertNotNull(resolvedBeforeMethod);
            assertNotNull(resolvedAfterMethod);
            assertEquals(expectedBeforeMethodName, resolvedBeforeMethod.getName());
            assertEquals(expectedAfterMethodName, resolvedAfterMethod.getName());
        }
    }


    private String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

}
