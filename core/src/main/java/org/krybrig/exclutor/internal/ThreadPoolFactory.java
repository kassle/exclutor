package org.krybrig.exclutor.internal;

import java.util.Queue;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author kassle
 */
public class ThreadPoolFactory {
    public static ThreadPool create(int maxThread, ThreadFactory threadFactory, Queue<Runnable> queue) {
        LockBox lockBox = new LockBoxImpl();
        ExclusiveWorkerFactory workerFactory = new ExclusiveWorkerFactoryImpl(queue, lockBox);
        return create(maxThread, workerFactory, threadFactory);
    }
    
    protected static ThreadPool create(int maxThread, ExclusiveWorkerFactory workerFactory, ThreadFactory threadFactory) {
        return new ThreadPoolImpl(maxThread, workerFactory, threadFactory);
    }
}
