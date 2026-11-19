package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;

import java.time.Duration;

/**
 * Used to measure elapsed time for execution.
 *
 * @author Tadaya Tsuyukubo
 * @see SystemStopwatchFactory.SystemStopwatch
 * @see NanoTimeStopwatchFactory.NanoTimeStopwatch
 * @see QueryExecutionContext#getElapsedTime()
 * @see MethodExecutionContext#getElapsedTime()
 * @since 1.5.1
 */
public interface Stopwatch {

    /**
     * Start the stopwatch.
     *
     * @return stopwatch
     */
    Stopwatch start();

    /**
     * Get the time from {@link #start()}.
     * Precision depends on the implementation class.
     *
     * @return elapsed time
     */
    Duration getElapsedTime();

}
