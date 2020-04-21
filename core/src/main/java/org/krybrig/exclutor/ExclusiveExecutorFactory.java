package org.krybrig.exclutor;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import org.krybrig.exclutor.internal.ExclusiveExecutor;
import org.krybrig.exclutor.internal.ExclusiveExecutorService;
import org.krybrig.exclutor.internal.RunnableFutureFactory;
import org.krybrig.exclutor.internal.ThreadPoolFactory;

/**
 * <h1>Exclusive Executor Factory</h1>
 * Factory to create exclusive executor instance
 * @author kassle
 */
public class ExclusiveExecutorFactory {

    /**
     * Create new ExclusiveExecutor instance with default ThreadFactory and Queue
     * @param maxThread maximum number of running thread
     * @throws IllegalArgumentException when maxThread is zero or lower
     * @return new ExclusiveExecutor instance
     */
    public static Executor create(int maxThread) {
        return create(maxThread, Executors.defaultThreadFactory(), new LinkedBlockingQueue<>());
    }

    /**
     * Create new ExclusiveExecutor instance with default ThreadFactory and Queue
     * @param maxThread maximum number of running thread
     * @param threadFactory An object that creates new threads on demand
     * @param queue the queue to use for holding tasks before they are executed
     * @throws IllegalArgumentException when maxThread is zero or lower
     * @throws NullPointerException when threadFactory or queue is null
     * @return new ExclusiveExecutor instance
     */
    public static Executor create(int maxThread, ThreadFactory threadFactory, Queue<Runnable> queue) {
        if (maxThread <= 0) {
            throw new IllegalArgumentException("maxThread should be > 0");
        }
        
        if (threadFactory == null) {
            throw new NullPointerException("ThreadFactory should not null");
        }
        
        if (queue == null) {
            throw new NullPointerException("Queue should not null");
        }

        return new ExclusiveExecutor(
                ThreadPoolFactory.create(maxThread, threadFactory, queue),
                queue);
    }
    
    /**
     * Create new ExclusiveExecutorService instance with default ThreadFactory and Queue
     * @param maxThread maximum number of running thread
     * @throws IllegalArgumentException when maxThread is zero or lower
     * @return new ExclusiveExecutorService instance
     */
    public static ExecutorService createExecutorService(int maxThread) {
        return createExecutorService(maxThread, Executors.defaultThreadFactory(), new LinkedBlockingQueue<>());
    }
    
    /**
     * Create new ExclusiveExecutorService instance with default ThreadFactory and Queue
     * @param maxThread maximum number of running thread
     * @param threadFactory An object that creates new threads on demand
     * @param queue the queue to use for holding tasks before they are executed
     * @throws IllegalArgumentException when maxThread is zero or lower
     * @throws NullPointerException when threadFactory or queue is null
     * @return new ExclusiveExecutorService instance
     */
    public static ExecutorService createExecutorService(int maxThread, ThreadFactory threadFactory, Queue<Runnable> queue) {
        ExclusiveExecutor executor = (ExclusiveExecutor) create(maxThread, threadFactory, queue);
        return new ExclusiveExecutorService(executor, new RunnableFutureFactory(), queue);
    }
}
