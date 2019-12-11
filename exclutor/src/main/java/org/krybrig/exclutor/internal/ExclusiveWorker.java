package org.krybrig.exclutor.internal;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
class ExclusiveWorker implements Runnable {

    private final Queue<Runnable> queue;
    private final LockBox lockBox;
    private final WorkerListener listener;

    ExclusiveWorker(Queue<Runnable> queue, LockBox lockBox, WorkerListener listener) {
        this.queue = queue;
        this.lockBox = lockBox;
        this.listener = listener;
    }

    @Override
    public void run() {
        Runnable runnable;
        Lock lock;

        synchronized (queue) {
            runnable = queue.poll();
        }

        if (runnable != null) {
            lock = getLock(runnable);
            lock.lock();
        } else {
            listener.onFinish();
            return;
        }

        if (!isExclusive(runnable)) {
            lock.unlock();
        }

        try {
            runnable.run();
        } finally {
            if (isExclusive(runnable)) {
                lock.unlock();
            }
            listener.onFinish();
        }
    }

    private Lock getLock(Runnable runnable) {
        if (runnable instanceof ExclusiveRunnable) {
            Lock lock = lockBox.getLock(((ExclusiveRunnable) runnable).getScope());
            return lock;
        } else {
            return new DummyLock();
        }
    }

    private boolean isExclusive(Runnable runnable) {
        return (runnable instanceof ExclusiveRunnable)
                && ((ExclusiveRunnable) runnable).isExclusive();
    }
}
