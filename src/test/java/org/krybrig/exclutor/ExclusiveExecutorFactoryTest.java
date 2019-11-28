package org.krybrig.exclutor;

import java.util.concurrent.ExecutorService;
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
        ThreadFactory threadFactory = EasyMock.createMock(ThreadFactory.class);
        
        ExecutorService executor1 = ExclusiveExecutorFactory.create(1, threadFactory);
        ExecutorService executor2 = ExclusiveExecutorFactory.create(1, threadFactory);
        
        assertNotNull(executor1);
        assertNotNull(executor2);
        assertNotSame(executor1, executor2);
    }
}
