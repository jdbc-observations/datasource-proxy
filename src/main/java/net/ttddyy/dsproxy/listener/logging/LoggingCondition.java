package net.ttddyy.dsproxy.listener.logging;

/**
 * Strategy to decide whether to perform logging logic.
 *
 * TODO: replace with BooleanSupplier once it moves java8
 *
 * @author Tadaya Tsuyukubo
 * @see AbstractQueryLoggingListener
 * @since 1.4.3
 */
public interface LoggingCondition {

    boolean getAsBoolean();

}
