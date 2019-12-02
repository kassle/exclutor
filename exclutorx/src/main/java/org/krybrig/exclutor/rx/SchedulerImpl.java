package org.krybrig.exclutor.rx;

import io.reactivex.Scheduler;
import java.util.concurrent.Executor;

/**
 *
 * @author kassle
 */
class SchedulerImpl extends Scheduler {
    private final Scheduler delayScheduler;
    private final Executor executor;
    private final boolean exclusive;
    private final String scope;

    SchedulerImpl(Scheduler delayScheduler, Executor executor, boolean exclusive, String scope) {
        this.delayScheduler = delayScheduler;
        this.executor = executor;
        this.exclusive = exclusive;
        this.scope = scope;
    }
    
    @Override
    public Worker createWorker() {
        return new WorkerImpl(delayScheduler, executor, exclusive, scope);
    }
}
