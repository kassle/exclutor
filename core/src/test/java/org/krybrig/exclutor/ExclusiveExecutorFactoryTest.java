package org.krybrig.exclutor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;
import org.krybrig.exclutor.internal.ExclusiveExecutor;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutorFactoryTest {
    
    @Test
    public void createWithParamMaxThreadShouldReturnNewObject() {
        Executor executor1 = ExclusiveExecutorFactory.create(2);
        Executor executor2 = ExclusiveExecutorFactory.create(2);
        
        assertNotNull(executor1);
        assertNotNull(executor2);
        assertNotSame(executor1, executor2);
    }
    
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
    
    @Test
    public void createExecutorServiceWithParamMaxThreadShouldReturnNewObject() {
        ExecutorService service1 = ExclusiveExecutorFactory.createExecutorService(2);
        ExecutorService service2 = ExclusiveExecutorFactory.createExecutorService(2);
        
        assertNotNull(service1);
        assertNotNull(service2);
        assertNotSame(service1, service2);
    }
    
    @Test
    public void createExecutorServiceShouldReturnNewObject() {
        Queue<Runnable> queue = EasyMock.createMock(Queue.class);
        ThreadFactory threadFactory = EasyMock.createMock(ThreadFactory.class);
        
        ExecutorService service1 = ExclusiveExecutorFactory.createExecutorService(1, threadFactory, queue);
        ExecutorService service2 = ExclusiveExecutorFactory.createExecutorService(1, threadFactory, queue);
        
        assertNotNull(service1);
        assertNotNull(service2);
        assertNotSame(service1, service2);
    }
    
    @Test
    public void createExecutorServiceWithCustomExecutorShouldReturnNewObject() {
        Queue<Runnable> queue = EasyMock.createMock(Queue.class);
        Executor executor = EasyMock.createMock(ExclusiveExecutor.class);
        
        ExecutorService service1 = ExclusiveExecutorFactory.createExecutorService(executor, queue);
        ExecutorService service2 = ExclusiveExecutorFactory.createExecutorService(executor, queue);
        
        assertNotNull(service1);
        assertNotNull(service2);
        assertNotSame(service1, service2);
    }
    
    @Test
    public void zeroMaxThreadShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ExclusiveExecutorFactory.create(0));
    }
    
    @Test
    public void negativeMaxThreadShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> ExclusiveExecutorFactory.create(-1));
    }
    
    @Test
    public void nullThreadFactoryShouldThrowNullpointerException() {
        Queue<Runnable> queue = EasyMock.createMock(Queue.class);
        assertThrows(NullPointerException.class, () -> ExclusiveExecutorFactory.create(1, null, queue));
    }
    
    @Test
    public void nullQueueShouldThrowNullpointerException() {
        ThreadFactory threadFactory = EasyMock.createMock(ThreadFactory.class);
        assertThrows(NullPointerException.class, () -> ExclusiveExecutorFactory.create(1, threadFactory, null));
    }
    
    @Test
    public void createExecutorServiceWithNonExclusiveExecutorShouldThrowIllegalArgumentException() {
        Queue<Runnable> queue = EasyMock.createMock(Queue.class);
        Executor executor = EasyMock.createMock(Executor.class);
        assertThrows(IllegalArgumentException.class, () -> ExclusiveExecutorFactory.createExecutorService(executor, queue));
    }
}
