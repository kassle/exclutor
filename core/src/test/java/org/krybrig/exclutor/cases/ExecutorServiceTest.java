package org.krybrig.exclutor.cases;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.krybrig.exclutor.AbstractExclusiveRunnable;
import org.krybrig.exclutor.ExclusiveExecutorFactory;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
public class ExecutorServiceTest {
    private static final String SCOPE = "scope.executor.service";
    private static final int MAX_THREAD = 2;
    private ExecutorService service;
    
    @Before
    public void setup() {
        service = ExclusiveExecutorFactory.createExecutorService(MAX_THREAD);
    }
    
    @Test
    public void basicUsage() {
        
        ExclusiveRunnable task1 = createTask("Hello ", "World");
        ExclusiveRunnable task2 = createTask("The World ", "is not enough");
        ExclusiveRunnable task3 = createTask("End ", "World");
        
        Future future1 = service.submit(task1);
        Future future2 = service.submit(task2);
        Future future3 = service.submit(task3);
        
        try {
            Thread.sleep(250);
        } catch (InterruptedException ex) { }
        
        service.shutdown();
        
        assertEquals(false, future1.isCancelled());
        assertEquals(false, future1.isDone());
        assertEquals(false, future2.isCancelled());
        assertEquals(false, future2.isDone());
        assertEquals(true, future3.isCancelled());
        assertEquals(true, future3.isDone());
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) { }
        
        assertEquals(false, future1.isCancelled());
        assertEquals(true, future1.isDone());
        assertEquals(false, future2.isCancelled());
        assertEquals(true, future2.isDone());
        assertEquals(true, future3.isCancelled());
        assertEquals(true, future3.isDone());
    }
    
    private ExclusiveRunnable createTask(String text1, String text2) {
        return new AbstractExclusiveRunnable(SCOPE, true) {
            @Override
            public void run() {
                System.out.print(text1);
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) { }
                
                System.out.println(text2);
            }
        };
    }
}
