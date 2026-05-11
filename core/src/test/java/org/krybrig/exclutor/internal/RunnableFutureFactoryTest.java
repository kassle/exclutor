package org.krybrig.exclutor.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.Future;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
public class RunnableFutureFactoryTest {
    private RunnableFutureFactory factory;
    
    @BeforeEach
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
