package org.krybrig.exclutor.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutorService implements ExecutorService {
    private final Object lock = new Object();

    private final ExclusiveExecutor executor;
    private final RunnableFutureFactory futureFactory;
    private final Queue<Runnable> queue;
    
    private boolean finish;

    public ExclusiveExecutorService(
            ExclusiveExecutor executor,
            RunnableFutureFactory futureFactory,
            Queue<Runnable> queue) {
        
        this.executor = executor;
        this.futureFactory = futureFactory;
        this.queue = queue;
    }

    @Override
    public void shutdown() {
        shutdownNow();
    }

    @Override
    public List<Runnable> shutdownNow() {
        finish = true;
        
        List<Runnable> taskList = new ArrayList<>();
        
        Runnable runnable = queue.poll();
        while (runnable != null) {
            if (runnable instanceof RunnableFuture) {
                ((RunnableFuture) runnable).cancel(true);
            }
            taskList.add(runnable);
            
            runnable = queue.poll();
        }
        
        synchronized (lock) {
            lock.notifyAll();
        }
        
        return taskList;
    }

    @Override
    public boolean isShutdown() {
        return finish;
    }

    @Override
    public boolean isTerminated() {
        return finish && queue.isEmpty();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        synchronized (lock) {
            lock.wait(TimeUnit.MILLISECONDS.convert(timeout, unit));
        }
        return isTerminated();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        throw new RejectedExecutionException("Not supported yet.");
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        throw new RejectedExecutionException("Not supported yet.");
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (task == null) {
            throw new NullPointerException("Task should not null");
        } else if (finish) {
            throw new RejectedExecutionException("Executor service already finished");
        } else {
            RunnableFuture future = futureFactory.createFuture(task);
            executor.execute(future);
            return future;
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new RejectedExecutionException("Not supported yet.");
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new RejectedExecutionException("Not supported yet.");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new RejectedExecutionException("Not supported yet.");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new RejectedExecutionException("Not supported yet.");
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("Task should not null");
        } else if (finish) {
            throw new RejectedExecutionException("Executor service already finished");
        } else {
            executor.execute(task);
        }
    }
}
