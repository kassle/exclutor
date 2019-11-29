package org.krybrig.exclutor.internal;

/**
 *
 * @author kassle
 */
public interface ExclusiveWorkerFactory {
    Runnable create(WorkerListener listener);
}
