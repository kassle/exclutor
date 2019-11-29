package org.krybrig.exclutor.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutorTest {
    private BlockingQueue<Runnable> queue;
    private ExclusiveWorkerFactory workerFactory;
    private ExclusiveExecutor executor;
    
    @Before
    public void setUp() {
        workerFactory = EasyMock.createMock(ExclusiveWorkerFactory.class);
        queue = new LinkedBlockingQueue<>();
        
        executor = new ExclusiveExecutor(
                workerFactory,
                1, 1, 0, TimeUnit.MILLISECONDS,
                queue,
                Executors.defaultThreadFactory());
    }

    @Test
    public void executeShouldCreateWorkerAndPlaceRunnableToQueue() {
        Runnable worker = EasyMock.createMock(Runnable.class);
        worker.run();
        EasyMock.replay(worker);
        
        WorkerListener listener = EasyMock.createMock(WorkerListener.class);
        EasyMock.expect(workerFactory.create(listener)).andReturn(worker);
        EasyMock.replay(workerFactory);
        
        Runnable runnable = EasyMock.createMock(Runnable.class);
        
        executor.submit(runnable);
        
        EasyMock.verify(workerFactory);
        
        Assert.assertEquals(1, queue.size());
        Assert.assertSame(runnable, queue.peek());
    }
    
    @Test (expected = NullPointerException.class)
    public void executeShouldThrowNullPointerWhenRunnableIsNull() {
        executor.execute(null);
    }
}
