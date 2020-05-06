package org.krybrig.exclutor.internal;

import java.util.Queue;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutorTest {
    private Queue<Runnable> queue;
    private ThreadPool pool;
    
    private ExclusiveExecutor executor;
    
    @Before
    public void setUp() {
        queue = EasyMock.createMock(Queue.class);
        pool = EasyMock.createMock(ThreadPool.class);
        
        executor = new ExclusiveExecutor(pool, queue);
    }
    
    @Test
    public void executeShouldAddToQueueAndCallThreadPoolTaskAdded() {
        Runnable task = EasyMock.createMock(Runnable.class);
        
        EasyMock.expect(queue.offer(task)).andReturn(true);
        pool.onTaskAdded();
        EasyMock.replay(queue, pool);
        
        executor.execute(task);
        
        EasyMock.verify(queue, pool);
    }
    
    @Test (expected = NullPointerException.class)
    public void executeShouldThrowNullPointerExceptionWhenTaskIsNull() {
        EasyMock.replay(queue, pool);
        
        executor.execute(null);
        
        EasyMock.verify(queue, pool);
    }
}
