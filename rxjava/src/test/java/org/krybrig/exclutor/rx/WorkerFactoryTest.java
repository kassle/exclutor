package org.krybrig.exclutor.rx;

import io.reactivex.rxjava3.core.Scheduler;
import java.util.concurrent.Executor;
import org.easymock.EasyMock;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kassle
 */
public class WorkerFactoryTest {
    private Scheduler delayScheduler;
    private WorkerFactory factory;
    
    @Before
    public void setUp() {
        delayScheduler = EasyMock.createMock(Scheduler.class);
        Executor executor = EasyMock.createMock(Executor.class);
        factory = new WorkerFactory(delayScheduler, executor);
    }

    @Test
    public void createShoulCreateWorkerInstance() {
        String scope = "my.scope.1";
        
        Scheduler.Worker delayWorker = EasyMock.createMock(Scheduler.Worker.class);
        EasyMock.expect(factory.create(scope, false)).andReturn(delayWorker);
        EasyMock.replay(delayScheduler);
        
        Scheduler.Worker worker = factory.create(scope, false);
        
        assertNotNull(worker);
        EasyMock.verify(delayScheduler);
    }
    
    @Test
    public void createShoulCreateWorkerImplInstance() {
        String scope = "my.scope.2";
        
        Scheduler.Worker delayWorker = EasyMock.createMock(Scheduler.Worker.class);
        EasyMock.expect(factory.create(scope, false)).andReturn(delayWorker);
        EasyMock.replay(delayScheduler);
        
        Scheduler.Worker worker = factory.create(scope, false);
        
        assertTrue(worker instanceof WorkerImpl);
        EasyMock.verify(delayScheduler);
    }
    
    @Test
    public void createShoulAlwaysCreateNewWorkerInstance() {
        String scope = "my.scope.3";
        
        Scheduler.Worker delayWorker = EasyMock.createMock(Scheduler.Worker.class);
        EasyMock.expect(factory.create(scope, false)).andReturn(delayWorker);
        EasyMock.expect(factory.create(scope, true)).andReturn(delayWorker);
        EasyMock.replay(delayScheduler);
        
        Scheduler.Worker workerRegular = factory.create(scope, false);
        Scheduler.Worker workerExclusive = factory.create(scope, true);
        
        assertNotSame(workerRegular, workerExclusive);
        EasyMock.verify(delayScheduler);
    }
}
