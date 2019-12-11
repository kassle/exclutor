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
    private final WorkerFactory workerFactory;

    public ExclusiveSchedulerFactory(int maxThread) {
        this(maxThread, Executors.defaultThreadFactory());
    }
    
    public ExclusiveSchedulerFactory(int maxThread, ThreadFactory threadFactory) {
        this(Schedulers.from(Executors.newSingleThreadScheduledExecutor()),
                ExclusiveExecutorFactory.create(maxThread, threadFactory, new LinkedBlockingQueue<>()));
    }
    
    ExclusiveSchedulerFactory(Scheduler delayScheduler, Executor executor) {
        this(new WorkerFactory(delayScheduler, executor));
    }

    ExclusiveSchedulerFactory(WorkerFactory workerFactory) {
        this.workerFactory = workerFactory;
    }
    
    public Scheduler createScheduler(String scope, boolean exclusive) {
        return new SchedulerImpl(workerFactory, exclusive, scope);
    }
}
