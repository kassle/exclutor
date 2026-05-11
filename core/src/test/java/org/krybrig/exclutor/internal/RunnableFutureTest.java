package org.krybrig.exclutor.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author kassle
 */
public class RunnableFutureTest {
    private Runnable delegate;
    private RunnableFuture future;
    
    @BeforeEach
    public void setUp() {
        delegate = EasyMock.createMock(Runnable.class);
        future = new RunnableFuture(delegate);
    }
    

    @Test
    public void runShouldPassToDelegateInstance() {
        delegate.run();
        EasyMock.replay(delegate);
        
        future.run();
        
        EasyMock.verify(delegate);
    }
    
    @Test
    public void cancelShouldChangeFlagCancelledTrue() {
        assertEquals(false, future.isCancelled());
        future.cancel(false);
        assertEquals(true, future.isCancelled());
    }
    
    @Test
    public void runShouldIgnoredWhenCancelFlagIsTrue() {
        EasyMock.replay(delegate);
        future.cancel(true);
        future.run();
        
        assertEquals(true, future.isCancelled());
        EasyMock.verify(delegate);
    }
    
    @Test
    public void isDoneShouldReturnFalseWhenRunIsNotFinished() {
        assertEquals(false, future.isDone());
    }
    
    @Test
    public void cancelShouldReturnFalseWhenAlreadyRunning() {
        delegate.run();
        EasyMock.expectLastCall().andAnswer(() -> {
            try {
                Thread.sleep(100);
            } catch(InterruptedException e) {
            }
            return null;
        });
        EasyMock.replay(delegate);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                future.run();
            }
        });
        
        thread.start();
        
        try {
            Thread.sleep(25);
        } catch (InterruptedException ex) { }
        boolean state = future.cancel(true);
        
        assertEquals(false, state);
    }
    
    @Test
    public void cancelShouldReturnFalseWhenAlreadyDone() {
        future.run();
        boolean state = future.cancel(true);
        
        assertEquals(false, state);
    }
    
    @Test
    public void successCancelShouldAlsoFlagAsDone() {
        future.cancel(true);
        
        assertEquals(true, future.isDone());
    }
    
    @Test
    public void getShouldReturnNull() throws InterruptedException, ExecutionException {
        assertEquals(null, future.get());
    }
    
    @Test
    public void getWithTimeOutShouldReturnNull() throws InterruptedException, ExecutionException, TimeoutException {
        assertEquals(null, future.get(1, TimeUnit.MINUTES));
    }
    
    @Test
    public void getDelegateShouldReturnDelegatedRunnable() {
        Runnable result = future.getDelegate();
        
        assertSame(delegate, result);
    }
}
