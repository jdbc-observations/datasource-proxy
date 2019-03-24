package net.ttddyy.dsproxy.proxy;

/**
 * Factory to create {@link SystemStopwatch} which uses {@code System.currentTimeMillis()}.
 *
 * The unit of time is milliseconds.
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
     * Uses {@code System.currentTimeMillis()} to calculate elapsed time.
     *
     * The unit of time is milliseconds
     */
    public static class SystemStopwatch implements Stopwatch {

        private long startTime;

        @Override
        public Stopwatch start() {
            this.startTime = System.currentTimeMillis();
            return this;
        }

        /**
         * Elapsed milliseconds from {@link #start()}.
         *
         * @return millisecond from {@link #start()}
         */
        @Override
        public long getElapsedTime() {
            return System.currentTimeMillis() - this.startTime;
        }

    }
}
