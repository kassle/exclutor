package org.krybrig.exclutor.rx;

import io.reactivex.rxjava3.core.Scheduler;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author kassle
 */
public class ExclusiveSchedulerFactoryTest {
    private ExclusiveSchedulerFactory schedulerFactory;
    
    @BeforeEach
    public void setUp() {
        WorkerFactory workerFactory = EasyMock.createMock(WorkerFactory.class);
        schedulerFactory = new ExclusiveSchedulerFactory(workerFactory);
    }
    
    @Test
    public void createFactoryInstanceWithMaxThreadShouldNotThrowAnyException() {
        ExclusiveSchedulerFactory factory = new ExclusiveSchedulerFactory(1);
        assertNotNull(factory);
    }
    
    @Test
    public void createFactoryInstanceWithZeroMaxThreadShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ExclusiveSchedulerFactory(0));
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
