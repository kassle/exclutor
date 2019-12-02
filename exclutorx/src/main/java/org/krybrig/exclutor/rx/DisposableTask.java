package org.krybrig.exclutor.rx;

import io.reactivex.disposables.Disposable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
class DisposableTask implements ExclusiveRunnable, Disposable {
    private final AtomicBoolean control = new AtomicBoolean(false);
    private final Runnable task;
    private final boolean exclusive;
    private final String scope;

    DisposableTask(Runnable task) {
        this.task = task;
        this.exclusive = false;
        this.scope = null;
    }
    
    DisposableTask(Runnable task, boolean exclusive, String scope) {
        this.task = task;
        this.exclusive = exclusive;
        this.scope = scope;
    }
    
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
        if (control.get()) {
            return;
        }
        
        try {
            executeTask();
        } finally {
            control.lazySet(true);
        }
    }
    
    public void executeTask() {
        task.run();
    }
    
    @Override
    public void dispose() {
        control.lazySet(true);
    }

    @Override
    public boolean isDisposed() {
        return control.get();
    }
    
    public void onFinish() { }
}
