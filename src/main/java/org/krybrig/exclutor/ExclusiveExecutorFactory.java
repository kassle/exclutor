package org.krybrig.exclutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.krybrig.exclutor.internal.ExclusiveExecutor;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutorFactory {
    public static ExecutorService create(int corePoolSize, ThreadFactory threadFactory) {
        return new ExclusiveExecutor(
                corePoolSize, corePoolSize,
                0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory);
    }
}
