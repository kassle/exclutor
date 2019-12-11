package org.krybrig.exclutor.rx;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
class WorkerImpl extends Scheduler.Worker {

    private final Scheduler.Worker delayWorker;
    private final Executor executor;
    private final boolean exclusive;
    private final String scope;

    WorkerImpl(Scheduler.Worker delayWorker, Executor executor, boolean exclusive, String scope) {
        this.delayWorker = delayWorker;
        this.executor = executor;
        this.exclusive = exclusive;
        this.scope = scope;
    }

    @Override
    public Disposable schedule(Runnable run) {
        return schedule(run, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public Disposable schedule(Runnable task, long delay, TimeUnit unit) {
        return delayWorker.schedule(new Runnable() {
            @Override
            public void run() {
                executeTask(task);
            }
        }, delay, unit);
    }

    @Override
    public Disposable schedulePeriodically(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return delayWorker.schedulePeriodically(new Runnable() {
            @Override
            public void run() {
                executeTask(task);
            }
        }, initialDelay, period, unit);
    }

    private void executeTask(Runnable task) {
        executor.execute(new ExclusiveRunnable() {
            @Override
            public String getScope() {
                return scope;
            }

            @Override
            public boolean isExclusive() {
                return exclusive;
            }

            @Override
            public void run() {
                task.run();
            }
        });
    }

    @Override
    public void dispose() {
        delayWorker.dispose();
    }

    @Override
    public boolean isDisposed() {
        return delayWorker.isDisposed();
    }
}
