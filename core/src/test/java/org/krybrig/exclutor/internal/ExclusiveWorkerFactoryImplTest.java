package org.krybrig.exclutor.internal;

import java.util.Queue;
import org.easymock.EasyMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kassle
 */
public class ExclusiveWorkerFactoryImplTest {
    private Queue<Runnable> queue;
    private LockBox lockBox;
    private WorkerListener listener;
    private ExclusiveWorkerFactory factory;
    
    @Before
    public void setUp() {
        queue = EasyMock.createMock(Queue.class);
        lockBox = EasyMock.createMock(LockBox.class);
        listener = EasyMock.createMock(WorkerListener.class);
        factory = new ExclusiveWorkerFactoryImpl(queue, lockBox);
    }

    @Test
    public void createShouldReturnWorker() {
        Runnable worker = factory.create(listener);
        
        assertNotNull(worker);
        assertEquals(true, worker instanceof ExclusiveWorker);
    }
    
    @Test
    public void createShouldAlwaysReturnNewWorkerInstance() {
        Runnable worker1 = factory.create(listener);
        Runnable worker2 = factory.create(listener);
        
        assertNotSame(worker1, worker2);
    }
    
    @Test(expected = NullPointerException.class)
    public void createShouldThrowNullPointerWhenQueueIsNull() {
        new ExclusiveWorkerFactoryImpl(null, lockBox);
    }
    
    @Test(expected = NullPointerException.class)
    public void createShouldThrowNullPointerWhenLockBoxIsNull() {
        new ExclusiveWorkerFactoryImpl(queue, null);
    }
}
