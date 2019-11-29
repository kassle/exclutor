package org.krybrig.exclutor;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import org.krybrig.exclutor.internal.ExclusiveExecutor;
import org.krybrig.exclutor.internal.ExclusiveWorkerFactory;
import org.krybrig.exclutor.internal.ExclusiveWorkerFactoryImpl;
import org.krybrig.exclutor.internal.LockBox;
import org.krybrig.exclutor.internal.LockBoxImpl;
import org.krybrig.exclutor.internal.ThreadPool;
import org.krybrig.exclutor.internal.ThreadPoolImpl;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutorFactory {
    public static Executor create(int maxThread) {
        return create(maxThread, Executors.defaultThreadFactory(), new LinkedBlockingQueue<>());
    }
    
    public static Executor create(int maxThread, ThreadFactory threadFactory, Queue<Runnable> queue) {
        LockBox lockBox = new LockBoxImpl();
        ExclusiveWorkerFactory workerFactory = new ExclusiveWorkerFactoryImpl(queue, lockBox);
        ThreadPool threadPool = new ThreadPoolImpl(maxThread, workerFactory, threadFactory);
        
        return new ExclusiveExecutor(threadPool, queue);
    }
}
