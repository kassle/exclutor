package org.krybrig.exclutor.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
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
    private LockBox lockBox;
    private BlockingQueue<Runnable> queue;
    private ExclusiveWorkerFactory workerFactory;
    private ExecutorService executor;
    
    @Before
    public void setUp() {
        lockBox = EasyMock.createMock(LockBox.class);
        workerFactory = EasyMock.createMock(ExclusiveWorkerFactory.class);
        queue = new LinkedBlockingQueue<>();
        
        executor = new ExclusiveExecutor(
                lockBox, workerFactory,
                1, 1, 0, TimeUnit.MILLISECONDS,
                queue,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory());
    }

    @Test
    public void executeShouldCreateWorkerAndPlaceRunnableToQueue() {
        Runnable worker = EasyMock.createMock(Runnable.class);
        worker.run();
        EasyMock.replay(worker);
        
        EasyMock.expect(workerFactory.create(queue, lockBox)).andReturn(worker);
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
