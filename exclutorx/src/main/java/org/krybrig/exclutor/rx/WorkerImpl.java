package org.krybrig.exclutor.rx;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author kassle
 */
class WorkerImpl extends Scheduler.Worker {
    private final Set<Disposable> disposableSet = new HashSet<>();
    private final AtomicBoolean dispose = new AtomicBoolean();
    
    private final Scheduler delayScheduler;
    private final Executor executor;
    private final boolean exclusive;
    private final String scope;

    WorkerImpl(Scheduler delayScheduler, Executor executor, boolean exclusive, String scope) {
        this.delayScheduler = delayScheduler;
        this.executor = executor;
        this.exclusive = exclusive;
        this.scope = scope;
    }

    @Override
    public Disposable schedule(Runnable run) {
        DisposableTask task = new DisposableTask(run, exclusive, scope) {
            @Override
            public void onFinish() {
                synchronized (disposableSet) {
                    disposableSet.remove(this);
                }
            }
        };
        synchronized(disposableSet) {
            disposableSet.add(task);
        }
        executor.execute(task);
        return task;
    }
    
    @Override
    public Disposable schedule(Runnable run, long delay, TimeUnit unit) {
        Disposable disposable = delayScheduler.scheduleDirect(new DisposableTask(run) {
            @Override
            public void run() {
                WorkerImpl.this.schedule(run);
            }
            
            @Override
            public void onFinish() {
                synchronized (disposableSet) {
                    disposableSet.remove(this);
                }
            }
        }, delay, unit);
        return disposable;
    }

    @Override
    public Disposable schedulePeriodically(Runnable run, long initialDelay, long period, TimeUnit unit) {
        Disposable disposable = delayScheduler.schedulePeriodicallyDirect(new DisposableTask(run) {
            @Override
            public void run() {
                WorkerImpl.this.schedule(run);
            }
            
            @Override
            public void onFinish() {
                synchronized (disposableSet) {
                    disposableSet.remove(this);
                }
            }
        }, initialDelay, period, unit);
        return disposable;
    }

    @Override
    public void dispose() {
        synchronized (disposableSet) {
            for (Disposable disposable : disposableSet) {
                disposable.dispose();
            }
            disposableSet.clear();
        }
        dispose.lazySet(true);
    }

    @Override
    public boolean isDisposed() {
        return dispose.get();
    }
}
