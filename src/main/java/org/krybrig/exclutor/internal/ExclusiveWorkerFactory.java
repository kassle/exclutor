package org.krybrig.exclutor.internal;

/**
 *
 * @author kassle
 */
interface ExclusiveWorkerFactory {
    Runnable create(WorkerListener listener);
}
