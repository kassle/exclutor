package org.krybrig.exclutor.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Queue;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author kassle
 */
public class ExclusiveWorkerFactoryImplTest {
    private Queue<Runnable> queue;
    private LockBox lockBox;
    private WorkerListener listener;
    private ExclusiveWorkerFactory factory;
    
    @BeforeEach
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
    
    @Test
    public void createShouldThrowNullPointerWhenQueueIsNull() {
        assertThrows(NullPointerException.class, () -> new ExclusiveWorkerFactoryImpl(null, lockBox));
    }
    
    @Test
    public void createShouldThrowNullPointerWhenLockBoxIsNull() {
        assertThrows(NullPointerException.class, () -> new ExclusiveWorkerFactoryImpl(queue, null));
    }
}
