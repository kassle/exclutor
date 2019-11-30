package org.krybrig.exclutor;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import org.krybrig.exclutor.internal.ExclusiveExecutor;
import org.krybrig.exclutor.internal.ThreadPoolFactory;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutorFactory {
    public static Executor create(int maxThread) {
        return create(maxThread, Executors.defaultThreadFactory(), new LinkedBlockingQueue<>());
    }
    
    public static Executor create(int maxThread, ThreadFactory threadFactory, Queue<Runnable> queue) {        
        return new ExclusiveExecutor(
                ThreadPoolFactory.create(maxThread, threadFactory, queue),
                queue);
    }
}
