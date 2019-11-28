package org.krybrig.exclutor.internal;

import java.util.Queue;

/**
 *
 * @author kassle
 */
public interface ExclusiveWorkerFactory {
    Runnable create(Queue<Runnable> queue, LockBox lockBox);
}
