package org.krybrig.exclutor.rx;

import io.reactivex.rxjava3.core.Scheduler;

/**
 *
 * @author kassle
 */
class SchedulerImpl extends Scheduler {
    private final WorkerFactory workerFactory;
    private final boolean exclusive;
    private final String scope;

    protected SchedulerImpl(WorkerFactory workerFactory, boolean exclusive, String scope) {
        this.workerFactory = workerFactory;
        this.exclusive = exclusive;
        this.scope = scope;
    }
    
    @Override
    public Worker createWorker() {
        return workerFactory.create(scope, exclusive);
    }
}
