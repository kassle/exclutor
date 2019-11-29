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
}
