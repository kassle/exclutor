package org.krybrig.exclutor.internal;

import java.util.Queue;

/**
 *
 * @author kassle
 */
public class ExclusiveWorkerFactoryImpl implements ExclusiveWorkerFactory {
    private final Queue<Runnable> queue;
    private final LockBox lockBox;

    ExclusiveWorkerFactoryImpl(Queue<Runnable> queue, LockBox lockBox) {
        if (queue == null || lockBox == null) {
            throw new NullPointerException("Queue, LockBox and Listener should not null");
        }
        
        this.queue = queue;
        this.lockBox = lockBox;
    }
    
    @Override
    public Runnable create(WorkerListener listener) {
        return new ExclusiveWorker(queue, lockBox, listener);
    }
    
}
