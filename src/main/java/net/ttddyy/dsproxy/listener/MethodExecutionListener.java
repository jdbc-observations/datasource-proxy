package net.ttddyy.dsproxy.listener;

/**
 * Callback listener for JDBC API method invocations.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.4.3
 */
public interface MethodExecutionListener {

    MethodExecutionListener DEFAULT = new NoOpMethodExecutionListener();

    void beforeMethod(MethodExecutionContext executionContext);

    void afterMethod(MethodExecutionContext executionContext);

}
