package org.krybrig.exclutor.rx;

import io.reactivex.Scheduler;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kassle
 */
public class SchedulerImplTest {
    private final String scope = "work.scope";
    private final boolean exclusive = true;
    private WorkerFactory workerFactory;
    private SchedulerImpl scheduler;
    
    @Before
    public void setUp() {
        workerFactory = EasyMock.createMock(WorkerFactory.class);
        scheduler = new SchedulerImpl(workerFactory, true, scope);
    }

    @Test
    public void createWorkerShouldUseWorkerFactory() {
        Scheduler.Worker worker = EasyMock.createMock(Scheduler.Worker.class);
        EasyMock.expect(workerFactory.create(scope, exclusive)).andReturn(worker);
        EasyMock.replay(workerFactory);
        
        Scheduler.Worker result = scheduler.createWorker();
        
        assertSame(worker, result);
        
        EasyMock.verify(workerFactory);
    }
    
}
