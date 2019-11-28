package org.krybrig.exclutor.internal;

import java.util.Queue;

/**
 *
 * @author kassle
 */
public class ExclusiveWorkerFactoryImpl implements ExclusiveWorkerFactory {
    
    @Override
    public Runnable create(Queue<Runnable> queue, LockBox lockBox) {
        if (queue != null && lockBox != null) {
            return new ExclusiveWorker(queue, lockBox);
        } else {
            throw new NullPointerException("Queue & LockBox should not null");
        }
    }
    
}
