package net.ttddyy.dsproxy.proxy;

import java.time.Duration;

/**
 * Factory for creating {@link NanoTimeStopwatch}.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.5.1
 */
public class NanoTimeStopwatchFactory implements StopwatchFactory {

    @Override
    public Stopwatch create() {
        return new NanoTimeStopwatch();
    }

    /**
     * {@link Stopwatch} implementation that uses {@code System.nanoTime()}.
     *
     * <p>{@link #getElapsedTime()} has nanosecond precision.
     */
    public static class NanoTimeStopwatch implements Stopwatch {

        private long startTime;

        @Override
        public Stopwatch start() {
            this.startTime = System.nanoTime();
            return this;
        }

        /**
         * Returns the elapsed time, with nanosecond precision, since {@link #start()}.
         *
         * @return elapsed time in nanoseconds since {@link #start()}
         */
        @Override
        public Duration getElapsedTime() {
            return Duration.ofNanos(System.nanoTime() - this.startTime);
        }

    }
}
