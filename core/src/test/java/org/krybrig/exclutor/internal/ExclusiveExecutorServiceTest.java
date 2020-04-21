package org.krybrig.exclutor.internal;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kassle
 */
public class ExclusiveExecutorServiceTest {
    private ExclusiveExecutor executor;
    private RunnableFutureFactory futureFactory;
    private ExclusiveExecutorService service;
    private Queue<Runnable> queue;
    
    @Before
    public void setUp() {
        executor = EasyMock.createMock(ExclusiveExecutor.class);
        futureFactory = EasyMock.createMock(RunnableFutureFactory.class);
        queue = EasyMock.createMock(Queue.class);
        
        service = new ExclusiveExecutorService(executor, futureFactory, queue);
    }

    @Test
    public void submitRunnableShouldExecuteAndReturnWrappedRunnableFuture() {
        Runnable task = EasyMock.createMock(Runnable.class);
        
        RunnableFuture expectFuture = EasyMock.createMock(RunnableFuture.class);
        EasyMock.expect(futureFactory.createFuture(task)).andReturn(expectFuture);
        
        executor.execute(expectFuture);
        
        EasyMock.replay(futureFactory, executor);
        
        Future future = service.submit(task);
        
        assertEquals(expectFuture, future);
        EasyMock.verify(executor);
    }
    
    @Test (expected = NullPointerException.class)
    public void submitNullShouldThrowNullPointerException() {
        service.submit((Runnable) null);
    }
    
    @Test (expected = RejectedExecutionException.class)
    public void submitAfterShutdownShouldThrowRejectedExecutionException() {
        Runnable task = EasyMock.createMock(Runnable.class);
        
        service.shutdown();
        service.submit(task);
    }
    
    @Test
    public void executeShoudDelegateToExecutor() {
        Runnable task = EasyMock.createMock(Runnable.class);
        
        executor.execute(task);
        EasyMock.replay(executor);
        
        service.execute(task);
        Executors.newCachedThreadPool();
        
        EasyMock.verify(executor);
    }
    
    @Test (expected = NullPointerException.class)
    public void executeNullShouldThrowNullPointerException() {
        service.execute(null);
    }
    
    @Test (expected = RejectedExecutionException.class)
    public void executeAfterShutdownShouldThrowRejectedExecutionException() {
        Runnable task = EasyMock.createMock(Runnable.class);
        
        service.shutdown();
        service.execute(task);
    }
    
    @Test
    public void shutdownShouldCancelAllTaskInQueue() {
        RunnableFuture task1 = EasyMock.createMock(RunnableFuture.class);
        EasyMock.expect(task1.cancel(true)).andReturn(Boolean.TRUE);
        
        RunnableFuture task2 = EasyMock.createMock(RunnableFuture.class);
        EasyMock.expect(task2.cancel(true)).andReturn(Boolean.TRUE);
        
        RunnableFuture task3 = EasyMock.createMock(RunnableFuture.class);
        EasyMock.expect(task3.cancel(true)).andReturn(Boolean.TRUE);
        
        EasyMock.expect(queue.poll())
                .andReturn(task1)
                .andReturn(task2)
                .andReturn(task3)
                .andReturn(null);
        
        EasyMock.replay(queue, task1, task2, task3);
        
        service.shutdown();
        
        EasyMock.verify(queue, task1, task2, task3);
    }
    
    @Test
    public void isShutdownShouldReturnFalse() {
        assertEquals(false, service.isShutdown());
    }
    
    @Test
    public void isShutdownShouldReturnTrueWhenShutdownInitiated() {
        service.shutdown();
        assertEquals(true, service.isShutdown());
    }
    
    @Test
    public void shutdownNowShouldReturnAllNotExecutedTask() {
        RunnableFuture task1 = EasyMock.createMock(RunnableFuture.class);
        EasyMock.expect(task1.cancel(true)).andReturn(Boolean.TRUE);
        
        Runnable task2 = EasyMock.createMock(Runnable.class);
        
        RunnableFuture task3 = EasyMock.createMock(RunnableFuture.class);
        EasyMock.expect(task3.cancel(true)).andReturn(Boolean.TRUE);
        
        Runnable[] expectArray = { task1, task2, task3 };
        
        EasyMock.expect(queue.poll())
                .andReturn(task1)
                .andReturn(task2)
                .andReturn(task3)
                .andReturn(null);
        
        EasyMock.replay(queue, task1, task2, task3);
        
        List<Runnable> taskList = service.shutdownNow();
        
        EasyMock.verify(queue, task1, task2, task3);
        assertEquals(true, service.isShutdown());
        assertArrayEquals( expectArray, taskList.toArray());
    }
    
    @Test
    public void isTerminatedShouldReturnFalseWhenNotFinished() {
        assertEquals(false, service.isTerminated());
    }
    
    @Test
    public void isTerminatedShouldReturnTrueWhenFinishedAndQueueEmpty() {
        EasyMock.expect(queue.poll()).andReturn(null);
        EasyMock.expect(queue.isEmpty()).andReturn(Boolean.TRUE);
        EasyMock.replay(queue);
        
        service.shutdown();
        
        assertEquals(true, service.isTerminated());
        EasyMock.verify(queue);
    }
    
    @Test
    public void awaitTerminationShouldWaitUntilExecutorServiceFinished() throws InterruptedException {
        RunnableFuture task = EasyMock.mock(RunnableFuture.class);
        EasyMock.expect(task.cancel(true)).andAnswer(new IAnswer<Boolean>() {
            @Override
            public Boolean answer() throws Throwable {
                Thread.sleep(10);
                return true;
            }
        });
        
        EasyMock.expect(queue.poll()).andReturn(task).andReturn(null);
        EasyMock.expect(queue.isEmpty()).andReturn(true);
        EasyMock.replay(task, queue);
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                service.shutdown();
            }
        }).start();
        
        boolean result = service.awaitTermination(100, TimeUnit.SECONDS);
        
        EasyMock.verify(task, queue);
        assertEquals(true, result);
    }
    
    @Test
    public void awaitTerminationShouldWaitUntilTimeOut() throws InterruptedException {
        boolean result = service.awaitTermination(100, TimeUnit.MILLISECONDS);
        assertEquals(false, result);
    }
    
    @Test (expected = RejectedExecutionException.class)
    public void submitCallableShouldThrowRejectedExceptionDueToNotYetSupportedFeature() {
        Callable callable = EasyMock.mock(Callable.class);
        service.submit(callable);
    }
    
    @Test (expected = RejectedExecutionException.class)
    public void submitRunnableWithResultShouldThrowRejectedExceptionDueToNotYetSupportedFeature() {
        Runnable runnable = EasyMock.mock(Runnable.class);
        Object result = EasyMock.mock(Object.class);
        service.submit(runnable, result);
    }
    
    @Test (expected = RejectedExecutionException.class)
    public void invokeAllShouldThrowRejectedExceptionDueToNotYetSupportedFeature() throws InterruptedException {
        service.invokeAll(Collections.EMPTY_LIST);
    }
    
    @Test (expected = RejectedExecutionException.class)
    public void invokeAllWithTimeOutShouldThrowRejectedExceptionDueToNotYetSupportedFeature() throws InterruptedException {
        service.invokeAll(Collections.EMPTY_LIST, 1, TimeUnit.DAYS);
    }
    
    @Test (expected = RejectedExecutionException.class)
    public void invokeAnyShouldThrowRejectedExceptionDueToNotYetSupportedFeature() throws InterruptedException, ExecutionException {
        service.invokeAny(Collections.EMPTY_SET);
    }
    
    @Test (expected = RejectedExecutionException.class)
    public void invokeAnyWithTimeOutShouldThrowRejectedExceptionDueToNotYetSupportedFeature() throws InterruptedException, ExecutionException, TimeoutException {
        service.invokeAny(Collections.EMPTY_SET, 1, TimeUnit.DAYS);
    }
}
