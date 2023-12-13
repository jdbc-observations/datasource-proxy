package net.ttddyy.dsproxy.proxy;

import java.util.concurrent.TimeUnit;

/**
 * Factory to create {@link SystemStopwatch} which uses monotonic time.
 * <p> The unit of time is milliseconds.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.5.1
 */
public class SystemStopwatchFactory implements StopwatchFactory {

    @Override
    public Stopwatch create() {
        return new SystemStopwatch();
    }

    /**
     * Uses monotonic time to calculate elapsed time.
     * <p>The unit of time is milliseconds.
     */
    public static class SystemStopwatch implements Stopwatch {

        private long startTime;

        @Override
        public Stopwatch start() {
            this.startTime = System.nanoTime();
            return this;
        }

        /**
         * Elapsed milliseconds from {@link #start()}.
         *
         * @return millisecond from {@link #start()}
         */
        @Override
        public long getElapsedTime() {
            return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.startTime);
        }

    }
}
