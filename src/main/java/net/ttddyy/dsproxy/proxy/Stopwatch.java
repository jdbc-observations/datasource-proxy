package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;

/**
 * Used to measure elapsed time for execution.
 *
 * @author Tadaya Tsuyukubo
 * @see SystemStopwatchFactory.SystemStopwatch
 * @see NanoTimeStopwatchFactory.NanoTimeStopwatch
 * @see ExecutionInfo#getElapsedTime()
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
     * The unit of returned time depends on the implementation class.
     *
     * @return elapsed time
     */
    long getElapsedTime();

}
