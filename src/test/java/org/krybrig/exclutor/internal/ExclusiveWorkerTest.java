package org.krybrig.exclutor.internal;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
public class ExclusiveWorkerTest {
    private Queue<Runnable> queue;
    private LockBox lockBox;
    private WorkerListener listener;
    private ExclusiveWorker worker;
    
    @Before
    public void setUp() {
        queue = EasyMock.createMock(Queue.class);
        lockBox = EasyMock.createMock(LockBox.class);
        listener = EasyMock.createMock(WorkerListener.class);
        
        worker = new ExclusiveWorker(queue, lockBox, listener);
    }

    @Test
    public void runShouldTakeQueueAndExecuteRunnableImmediatelyWhenBasicRunnable() {
        Runnable runnable = EasyMock.createMock(Runnable.class);
        runnable.run();
        EasyMock.replay(runnable);

        EasyMock.expect(queue.poll()).andReturn(runnable);
        EasyMock.replay(queue);
        
        listener.onFinish();
        EasyMock.replay(listener);
        
        worker.run();
        
        EasyMock.verify(runnable);
        EasyMock.verify(listener);
    }
    
    @Test
    public void runShouldTakeQueueAndReturnImmediatelyWhenRunnableIsNull() {
        EasyMock.expect(queue.poll()).andReturn(null);
        EasyMock.replay(queue);
        
        listener.onFinish();
        EasyMock.replay(listener);
        
        worker.run();
        
        EasyMock.verify(queue);
        EasyMock.verify(listener);
    }
    
    @Test
    public void runShouldManageQueueBeforeExecuteRunnableWhenExclusiveRunnable() {
        String scope = "db.profile";
        ExclusiveRunnable runnable = EasyMock.createMock(ExclusiveRunnable.class);
        EasyMock.expect(runnable.getScope()).andReturn(scope);
        EasyMock.expect(runnable.isExclusive()).andStubReturn(true);
        runnable.run();
        EasyMock.replay(runnable);

        Lock lock = EasyMock.createMock(Lock.class);
        lock.lock();
        lock.unlock();
        EasyMock.replay(lock);
        
        EasyMock.expect(lockBox.getLock(scope)).andReturn(lock);
        EasyMock.replay(lockBox);
        
        EasyMock.expect(queue.poll()).andReturn(runnable);
        EasyMock.replay(queue);
        
        listener.onFinish();
        EasyMock.replay(listener);
        
        worker.run();
        
        EasyMock.verify(runnable);
        EasyMock.verify(lock);
        EasyMock.verify(listener);
    }
    
    @Test 
    public void onRunnableExceptionShouldUnlockToPreventDeadlock() {
        ExclusiveRunnable runnable = new ExclusiveRunnable() {
            @Override
            public String getScope() {
                return "db.company";
            }

            @Override
            public boolean isExclusive() {
                return true;
            }

            @Override
            public void run() {
                throw new RuntimeException("This error shall not break the executor system");
            }
        };
        
        Lock lock = EasyMock.createMock(Lock.class);
        lock.lock();
        lock.unlock();
        EasyMock.replay(lock);
        
        EasyMock.expect(lockBox.getLock(runnable.getScope())).andReturn(lock);
        EasyMock.replay(lockBox);
        
        EasyMock.expect(queue.poll()).andReturn(runnable);
        EasyMock.replay(queue);
        
        listener.onFinish();
        EasyMock.replay(listener);
        
        try {
            worker.run();
        } catch (RuntimeException ex) { }
        
        EasyMock.verify(lock);
        EasyMock.verify(listener);
    }
}
