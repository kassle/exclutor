package org.krybrig.exclutor.internal;

import java.util.concurrent.Future;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
public class RunnableFutureFactoryTest {
    private RunnableFutureFactory factory;
    
    @Before
    public void setUp() {
        factory = new RunnableFutureFactory();
    }

    @Test
    public void createRunnableFutureWithStandardRunnableInputShouldReturnStandardRunnableFuture() {
        Runnable runnable = EasyMock.createMock(Runnable.class);
        
        Future future = factory.createFuture(runnable);
        
        assertEquals(true, future instanceof RunnableFuture);
        assertEquals(false, future instanceof ExclusiveRunnableFuture);
    }
    
    @Test
    public void createRunnableFutureWithExclusiveRunnableInputShouldReturnExclusiveRunnableFuture() {
        Runnable runnable = EasyMock.createMock(ExclusiveRunnable.class);
        
        Future future = factory.createFuture(runnable);
        
        assertEquals(true, future instanceof ExclusiveRunnableFuture);
    }
}
