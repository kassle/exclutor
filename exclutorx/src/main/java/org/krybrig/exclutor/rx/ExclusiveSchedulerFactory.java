package org.krybrig.exclutor.rx;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import org.krybrig.exclutor.ExclusiveExecutorFactory;

/**
 *
 * @author kassle
 */
public class ExclusiveSchedulerFactory {
    private final Scheduler delayScheduler;
    private final Executor executor;

    public ExclusiveSchedulerFactory(int maxThread) {
        this(maxThread, Executors.defaultThreadFactory());
    }
    
    public ExclusiveSchedulerFactory(int maxThread, ThreadFactory threadFactory) {
        this(Schedulers.single(),
                ExclusiveExecutorFactory.create(maxThread, threadFactory, new LinkedBlockingQueue<>()));
    }
    
    ExclusiveSchedulerFactory(Scheduler delayScheduler, Executor executor) {
        this.delayScheduler = delayScheduler;
        this.executor = executor;
    }
    
    public Scheduler createScheduler(String scope, boolean isExclusive) {
        return new SchedulerImpl(delayScheduler, executor, isExclusive, scope);
    }
}
