package org.krybrig.exclutor.rx;

import java.util.concurrent.Executor;

import io.reactivex.rxjava3.core.Scheduler;

/**
 *
 * @author kassle
 */
class WorkerFactory {
    private final Scheduler delayScheduler;
    private final Executor executor;

    public WorkerFactory(Scheduler delayScheduler, Executor executor) {
        this.delayScheduler = delayScheduler;
        this.executor = executor;
    }
    
    protected Scheduler.Worker create(String scope, boolean exclusive) {
        return new WorkerImpl(delayScheduler.createWorker(), executor, exclusive, scope);
    }
}
