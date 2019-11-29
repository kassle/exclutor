package org.krybrig.exclutor.internal;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutor extends ThreadPoolExecutor {
    private final Queue<Runnable> queue;
    private final ExclusiveWorkerFactory workerFactory;
    
    public ExclusiveExecutor(
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> queue,
            ThreadFactory threadFactory) {
        
        this(new ExclusiveWorkerFactoryImpl(queue, new LockBoxImpl()),
                corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, threadFactory);
    }

    public ExclusiveExecutor(
            ExclusiveWorkerFactory workerFactory,
            int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> queue,
            ThreadFactory threadFactory) {
        
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, threadFactory);
        
        this.workerFactory = workerFactory;
        this.queue = queue;
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        
        synchronized (queue) {
            queue.offer(task);
        }
        
        return super.submit(workerFactory.create(new WorkerListener() {
            @Override
            public void onFinish() {
            }
        }));
    }
}
