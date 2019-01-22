package net.ttddyy.dsproxy.proxy;

/**
 * Used to measure elapsed time for execution.
 *
 * @author Tadaya Tsuyukubo
 * @see SystemStopwatchFactory.SystemStopwatch
 * @see NanoTimeStopwatchFactory.NanoTimeStopwatch
 * @see net.ttddyy.dsproxy.listener.QueryExecutionContext#getElapsedTime()
 * @see net.ttddyy.dsproxy.listener.MethodExecutionContext#getElapsedTime()
 * @since 1.6
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
