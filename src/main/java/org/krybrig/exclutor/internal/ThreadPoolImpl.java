package org.krybrig.exclutor.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author kassle
 */
public class ThreadPoolImpl implements ThreadPool, WorkerListener {

    private final int maxThread;
    private final ExclusiveWorkerFactory workerFactory;
    private final ThreadFactory threadFactory;

    private final AtomicInteger taskCounter = new AtomicInteger();
    private final AtomicInteger threadCounter = new AtomicInteger();

    public ThreadPoolImpl(int maxThread, ExclusiveWorkerFactory workerFactory, ThreadFactory threadFactory) {
        this.maxThread = maxThread;
        this.workerFactory = workerFactory;
        this.threadFactory = threadFactory;
    }

    @Override
    public void onTaskAdded() {
        synchronized (taskCounter) {
            taskCounter.getAndIncrement();

            synchronized (threadCounter) {
                startNewThread();
            }
        }
    }

    @Override
    public int getRemainingTask() {
        synchronized (taskCounter) {
            return taskCounter.get();
        }
    }

    @Override
    public int getThreadNumber() {
        synchronized (threadCounter) {
            return threadCounter.get();
        }
    }

    @Override
    public void onFinish() {
        synchronized (threadCounter) {
            threadCounter.getAndDecrement();

            synchronized (taskCounter) {
                startNewThread();
            }
        }
    }

    private void startNewThread() {
        if (taskCounter.get() > 0 && threadCounter.get() < maxThread) {
            Thread thread = threadFactory.newThread(workerFactory.create(this));
            threadCounter.getAndIncrement();
            taskCounter.getAndDecrement();
            thread.start();
        }
    }
}
