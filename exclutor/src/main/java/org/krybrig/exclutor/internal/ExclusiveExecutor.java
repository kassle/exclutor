package org.krybrig.exclutor.internal;

import java.util.Queue;
import java.util.concurrent.Executor;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutor implements Executor {
    private final ThreadPool pool;
    private final Queue<Runnable> queue;

    public ExclusiveExecutor(ThreadPool pool, Queue<Runnable> queue) {
        this.pool = pool;
        this.queue = queue;
    }    

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("Task should not null");
        }

        synchronized (queue) {
            queue.offer(task);
        }

        pool.onTaskAdded();
    }
}
