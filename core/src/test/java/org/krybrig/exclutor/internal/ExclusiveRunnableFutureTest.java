package org.krybrig.exclutor.internal;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
public class ExclusiveRunnableFutureTest {
    private ExclusiveRunnable delegate;
    private ExclusiveRunnableFuture future;

    @Before
    public void setUp() {
        delegate = EasyMock.createMock(ExclusiveRunnable.class);
        future = new ExclusiveRunnableFuture(delegate);
    }

    @Test
    public void getScopeShouldPassToDelegateInstance() {
        String expectScope = "delegate.scope";
        EasyMock.expect(delegate.getScope()).andReturn(expectScope);
        EasyMock.replay(delegate);
        
        String scope = future.getScope();
        
        assertSame(expectScope, scope);
        EasyMock.verify(delegate);
    }
    
    @Test
    public void isExclusiveShouldPassToDelegateInstance() {
        boolean expectExclusive = true;
        EasyMock.expect(delegate.isExclusive()).andReturn(expectExclusive);
        EasyMock.replay(delegate);
        
        Boolean exclusive = future.isExclusive();
        
        assertEquals(expectExclusive, exclusive);
        EasyMock.verify(delegate);
    }
}
