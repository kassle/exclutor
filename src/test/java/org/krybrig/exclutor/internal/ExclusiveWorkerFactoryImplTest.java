package org.krybrig.exclutor.internal;

import java.util.Queue;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kassle
 */
public class ExclusiveWorkerFactoryImplTest {
    private Queue<Runnable> queue;
    private LockBox lockBox;
    private ExclusiveWorkerFactory factory;
    
    @Before
    public void setUp() {
        queue = EasyMock.createMock(Queue.class);
        lockBox = EasyMock.createMock(LockBox.class);
        factory = new ExclusiveWorkerFactoryImpl();
    }

    @Test
    public void createShouldReturnWorker() {
        Runnable worker = factory.create(queue, lockBox);
        
        assertNotNull(worker);
        assertEquals(true, worker instanceof ExclusiveWorker);
    }
    
    @Test
    public void createShouldAlwaysReturnNewWorkerInstance() {
        Runnable worker1 = factory.create(queue, lockBox);
        Runnable worker2 = factory.create(queue, lockBox);
        
        assertNotSame(worker1, worker2);
    }
    
    @Test(expected = NullPointerException.class)
    public void createShouldThrowNullPointerWhenQueueIsNull() {
        factory.create(null, lockBox);
    }
    
    @Test(expected = NullPointerException.class)
    public void createShouldThrowNullPointerWhenLockBoxIsNull() {
        factory.create(queue, null);
    }
}
