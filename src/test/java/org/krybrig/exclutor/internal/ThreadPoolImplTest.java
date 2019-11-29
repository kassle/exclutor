package org.krybrig.exclutor.internal;

import java.util.concurrent.ThreadFactory;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kassle
 */
public class ThreadPoolImplTest {
    private int max = 2;
    private ExclusiveWorkerFactory workerFactory;
    private ThreadFactory threadFactory;
    private ThreadPoolImpl pool;
    
    @Before
    public void setUp() {
        workerFactory = EasyMock.createMock(ExclusiveWorkerFactory.class);
        threadFactory = EasyMock.createMock(ThreadFactory.class);
        
        pool = new ThreadPoolImpl(max, workerFactory, threadFactory);
    }
    
    @Test
    public void onTaskAddedShouldSetupThreadImmediatelyWhenThreadNumberBelowMax() {
        Runnable worker = EasyMock.createMock(Runnable.class);
        
        EasyMock.expect(workerFactory.create(pool)).andReturn(worker);
        EasyMock.replay(workerFactory);
        
        Thread thread = EasyMock.createMock(Thread.class);
        thread.start();
        EasyMock.replay(thread);
        
        EasyMock.expect(threadFactory.newThread(worker)).andReturn(thread);
        EasyMock.replay(threadFactory);
        
        pool.onTaskAdded();
        
        assertEquals(1, pool.getThreadNumber());
        assertEquals(0, pool.getRemainingTask());
        EasyMock.verify(thread);
    }
    
    @Test
    public void onTaskAddedShouldSetupThreadImmediatelyUntilMaxThreadNumber() {
        Runnable worker = EasyMock.createMock(Runnable.class);
        
        EasyMock.expect(workerFactory.create(pool)).andStubReturn(worker);
        EasyMock.replay(workerFactory);
        
        Thread thread1 = EasyMock.createMock(Thread.class);
        thread1.start();
        EasyMock.replay(thread1);
        
        Thread thread2 = EasyMock.createMock(Thread.class);
        thread2.start();
        EasyMock.replay(thread2);
        
        EasyMock.expect(threadFactory.newThread(worker))
                .andReturn(thread1)
                .andReturn(thread2);
        EasyMock.replay(threadFactory);
        
        pool.onTaskAdded();
        pool.onTaskAdded();
        pool.onTaskAdded();
        
        assertEquals(1, pool.getRemainingTask());
        assertEquals(max, pool.getThreadNumber());
        EasyMock.verify(thread1, thread2);
    }

    @Test
    public void onFinishShouldDecreaseThreadNumber() {
        Runnable worker = EasyMock.createMock(Runnable.class);
        EasyMock.expect(workerFactory.create(pool)).andReturn(worker);
        EasyMock.replay(workerFactory);
        
        Thread thread = EasyMock.createMock(Thread.class);        
        EasyMock.expect(threadFactory.newThread(worker)).andReturn(thread);
        EasyMock.replay(threadFactory);
        
        pool.onTaskAdded();
        pool.onFinish();
        
        assertEquals(0, pool.getThreadNumber());
    }
    
    @Test
    public void onFinishShouldSetupThreadWhenTaskNotEmpty() {
        Runnable worker = EasyMock.createMock(Runnable.class);
        EasyMock.expect(workerFactory.create(pool)).andStubReturn(worker);
        EasyMock.replay(workerFactory);
        
        Thread thread = EasyMock.createMock(Thread.class);
        EasyMock.expect(threadFactory.newThread(worker)).andStubReturn(thread);
        EasyMock.replay(threadFactory);
        
        pool.onTaskAdded();
        pool.onTaskAdded();
        pool.onTaskAdded();
        pool.onTaskAdded();
        
        assertEquals(2, pool.getThreadNumber());
        assertEquals(2, pool.getRemainingTask());
        
        pool.onFinish();
        
        assertEquals(2, pool.getThreadNumber());
        assertEquals(1, pool.getRemainingTask());
    }
}
