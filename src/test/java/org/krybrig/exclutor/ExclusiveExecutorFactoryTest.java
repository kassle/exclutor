package org.krybrig.exclutor;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import org.easymock.EasyMock;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutorFactoryTest {
    
    @Test
    public void createShouldReturnNewObject() {
        Queue<Runnable> queue = EasyMock.createMock(Queue.class);
        ThreadFactory threadFactory = EasyMock.createMock(ThreadFactory.class);
        
        Executor executor1 = ExclusiveExecutorFactory.create(1, threadFactory, queue);
        Executor executor2 = ExclusiveExecutorFactory.create(1, threadFactory, queue);
        
        assertNotNull(executor1);
        assertNotNull(executor2);
        assertNotSame(executor1, executor2);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void zeroMaxThreadShouldThrowIllegalArgumentException() {
        ExclusiveExecutorFactory.create(0);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void negativeMaxThreadShouldThrowIllegalArgumentException() {
        ExclusiveExecutorFactory.create(-1);
    }
    
    @Test (expected = NullPointerException.class)
    public void nullThreadFactoryShouldThrowNullpointerException() {
        Queue<Runnable> queue = EasyMock.createMock(Queue.class);
        ExclusiveExecutorFactory.create(1, null, queue);
    }
    
    @Test (expected = NullPointerException.class)
    public void nullQueueShouldThrowNullpointerException() {
        ThreadFactory threadFactory = EasyMock.createMock(ThreadFactory.class);
        ExclusiveExecutorFactory.create(1, threadFactory, null);
    }
}
