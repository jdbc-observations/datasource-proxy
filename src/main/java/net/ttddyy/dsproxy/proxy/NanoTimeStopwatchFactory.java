package net.ttddyy.dsproxy.proxy;

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
     * <p>{@link #getElapsedTime()} returns nanoseconds.
     */
    public static class NanoTimeStopwatch implements Stopwatch {

        private long startTime;

        @Override
        public Stopwatch start() {
            this.startTime = System.nanoTime();
            return this;
        }

        /**
         * Returns the elapsed time in nanoseconds since {@link #start()}.
         *
         * @return elapsed time in nanoseconds since {@link #start()}
         */
        @Override
        public long getElapsedTime() {
            return System.nanoTime() - this.startTime;
        }

    }
}
