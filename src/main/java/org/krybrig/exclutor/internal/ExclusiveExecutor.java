package org.krybrig.exclutor.internal;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutor extends ThreadPoolExecutor {
    private final LockBox lockBox;
    private final Queue<Runnable> runnableQueue;
    private final ExclusiveWorkerFactory workerFactory;
    
    public ExclusiveExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> queue,
            ThreadFactory threadFactory) {
        
        this(new LockBoxImpl(), new ExclusiveWorkerFactoryImpl(),
                corePoolSize, maximumPoolSize, keepAliveTime, unit,
                new LinkedBlockingQueue<Runnable>(), queue, threadFactory);
    }

    public ExclusiveExecutor(
            LockBox lockBox,
            ExclusiveWorkerFactory workerFactory,
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> runnableQueue,
            BlockingQueue<Runnable> queue,
            ThreadFactory threadFactory) {
        
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, threadFactory);
        
        this.lockBox = lockBox;
        this.workerFactory = workerFactory;
        this.runnableQueue = runnableQueue;
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        
        synchronized (runnableQueue) {
            runnableQueue.offer(task);
        }
        
        return super.submit(workerFactory.create(runnableQueue, lockBox));
    }
}
