package net.ttddyy.dsproxy.proxy;

/**
 * Factory to create {@link NanoTimeStopwatch}.
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
     * {@link #getElapsedTime()} returns nano seconds.
     */
    public static class NanoTimeStopwatch implements Stopwatch {

        private long startTime;

        @Override
        public Stopwatch start() {
            this.startTime = System.nanoTime();
            return this;
        }

        /**
         * Elapsed nano seconds from {@link #start()}.
         *
         * @return nano second from {@link #start()}
         */
        @Override
        public long getElapsedTime() {
            return System.nanoTime() - this.startTime;
        }

    }
}
