package org.krybrig.exclutor.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author kassle
 */
class RunnableFuture<V> implements Runnable, Future<V>{
    private boolean cancel;
    private boolean running;
    private boolean finish;
    
    private final Runnable delegate;

    RunnableFuture(Runnable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void run() {
        if (!cancel) {
            try {
                running = true;
                delegate.run();
            } finally {
                finish = true;
            }
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (!running && !finish) {
            cancel = true;
            finish = true;
        }
        return cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public boolean isDone() {
        return finish;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    Runnable getDelegate() {
        return delegate;
    }
}
