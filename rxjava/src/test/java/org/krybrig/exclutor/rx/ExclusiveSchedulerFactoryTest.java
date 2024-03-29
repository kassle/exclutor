package org.krybrig.exclutor.rx;

import io.reactivex.rxjava3.core.Scheduler;
import org.easymock.EasyMock;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author kassle
 */
public class ExclusiveSchedulerFactoryTest {
    private ExclusiveSchedulerFactory schedulerFactory;
    
    @Before
    public void setUp() {
        WorkerFactory workerFactory = EasyMock.createMock(WorkerFactory.class);
        schedulerFactory = new ExclusiveSchedulerFactory(workerFactory);
    }
    
    @Test
    public void createFactoryInstanceWithMaxThreadShouldNotThrowAnyException() {
        ExclusiveSchedulerFactory factory = new ExclusiveSchedulerFactory(1);
        assertNotNull(factory);
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void createFactoryInstanceWithZeroMaxThreadShouldThrowIllegalArgumentException() {
        ExclusiveSchedulerFactory factory = new ExclusiveSchedulerFactory(0);
        assertNotNull(factory);
    }
    
    @Test
    public void createShouldCreateSchedulerInstance() {
        String scope = "scheduler.scope.1";
        
        Scheduler scheduler = schedulerFactory.createScheduler(scope, true);
        
        assertNotNull(scheduler);
    }
    
    @Test
    public void createShouldCreateSchedulerImplInstance() {
        String scope = "scheduler.scope.2";
        
        Scheduler scheduler = schedulerFactory.createScheduler(scope, false);
        
        assertTrue(scheduler instanceof SchedulerImpl);
    }
    
    @Test
    public void createShouldAlwaysCreateNewInstance() {
        String scope = "scheduler.scope.3";
        
        Scheduler scheduler1 = schedulerFactory.createScheduler(scope, true);
        Scheduler scheduler2 = schedulerFactory.createScheduler(scope, false);
        
        assertNotSame(scheduler1, scheduler2);
    }
}
