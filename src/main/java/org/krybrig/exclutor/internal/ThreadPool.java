package org.krybrig.exclutor.internal;

/**
 *
 * @author kassle
 */
public interface ThreadPool {
    void onTaskAdded();
    int getRemainingTask();
    int getThreadNumber();
}
