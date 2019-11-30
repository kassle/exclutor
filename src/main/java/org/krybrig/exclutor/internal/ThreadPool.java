package org.krybrig.exclutor.internal;

/**
 *
 * @author kassle
 */
interface ThreadPool {
    void onTaskAdded();
    int getRemainingTask();
    int getThreadNumber();
}
