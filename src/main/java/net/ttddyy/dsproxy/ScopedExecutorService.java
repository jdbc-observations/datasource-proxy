package net.ttddyy.dsproxy;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * {@link ExecutorService} that wraps every task with {@link QueryCounter#wrap(Callable)} /
 * {@link QueryCounter#wrap(Runnable)} at submit time.
 */
final class ScopedExecutorService implements ExecutorService {

    private final ExecutorService delegate;

    ScopedExecutorService(ExecutorService delegate) {
        this.delegate = delegate;
    }

    private static <T> List<Callable<T>> wrapAll(Collection<? extends Callable<T>> tasks) {
        return tasks.stream().map(QueryCounter::wrap).toList();
    }

    @Override
    public void execute(Runnable command) {
        this.delegate.execute(QueryCounter.wrap(command));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.delegate.submit(QueryCounter.wrap(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.delegate.submit(QueryCounter.wrap(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.delegate.submit(QueryCounter.wrap(task));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.delegate.invokeAll(wrapAll(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        return this.delegate.invokeAll(wrapAll(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.delegate.invokeAny(wrapAll(tasks));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate.invokeAny(wrapAll(tasks), timeout, unit);
    }

    @Override
    public void shutdown() {
        this.delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.awaitTermination(timeout, unit);
    }

}
