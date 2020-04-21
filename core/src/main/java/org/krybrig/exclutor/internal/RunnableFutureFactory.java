package org.krybrig.exclutor.internal;

import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
public class RunnableFutureFactory {
    public RunnableFuture createFuture(Runnable task) {
        if (task instanceof ExclusiveRunnable) {
            return new ExclusiveRunnableFuture((ExclusiveRunnable) task);
        } else {
            return new RunnableFuture(task);
        }
    }
}
