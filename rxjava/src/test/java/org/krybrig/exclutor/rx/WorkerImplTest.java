package org.krybrig.exclutor.rx;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
public class WorkerImplTest {
    private String scope = "worker.scope";
    private boolean exclusive = false;
    private Scheduler.Worker delayWorker;
    private Executor executor;
    
    private WorkerImpl worker;
    
    @Before
    public void setUp() {
        delayWorker = EasyMock.createMock(Scheduler.Worker.class);
        executor = EasyMock.createMock(Executor.class);
        
        worker = new WorkerImpl(delayWorker, executor, exclusive, scope);
    }
    
    @Test
    public void scheduleDriectShouldDelegateToDelayWorkerWithZeroDelay() {
        Runnable run = EasyMock.createMock(Runnable.class);
        Disposable disposable = EasyMock.createMock(Disposable.class);
        
        EasyMock.expect(delayWorker.schedule(
                EasyMock.anyObject(Runnable.class),
                EasyMock.eq(0L), EasyMock.same(TimeUnit.MILLISECONDS)))
                .andReturn(disposable);
        EasyMock.replay(delayWorker);
        
        Disposable result = worker.schedule(run);
        
        assertSame(disposable, result);
        EasyMock.verify(delayWorker);
    }
    
    @Test
    public void scheduleShouldDelegateToDelayWorker() {
        long delay = 5L;
        TimeUnit unit = TimeUnit.DAYS;
        Runnable run = EasyMock.createMock(Runnable.class);
        Disposable disposable = EasyMock.createMock(Disposable.class);
        
        EasyMock.expect(delayWorker.schedule(
                EasyMock.anyObject(Runnable.class),
                EasyMock.eq(delay), EasyMock.same(unit)))
                .andReturn(disposable);
        EasyMock.replay(delayWorker);
        
        Disposable result = worker.schedule(run, delay, unit);
        
        assertSame(disposable, result);
        EasyMock.verify(delayWorker);
    }
    
    @Test
    public void schedulePeriodicallyShouldDelegateToDelayWorker() {
        long delay = 5L;
        long period = 1L;
        TimeUnit unit = TimeUnit.DAYS;
        Runnable run = EasyMock.createMock(Runnable.class);
        Disposable disposable = EasyMock.createMock(Disposable.class);
        
        EasyMock.expect(delayWorker.schedulePeriodically(
                EasyMock.anyObject(Runnable.class),
                EasyMock.eq(delay), EasyMock.eq(period), EasyMock.same(unit)))
                .andReturn(disposable);
        EasyMock.replay(delayWorker);
        
        Disposable result = worker.schedulePeriodically(run, delay, period, unit);
        
        assertSame(disposable, result);
        EasyMock.verify(delayWorker);
    }

    @Test
    public void onTaskExecuteViaScheduleShouldExecuteUsingExecutor() {
        long delay = 5L;
        TimeUnit unit = TimeUnit.DAYS;
        Runnable run = EasyMock.createMock(Runnable.class);
        Disposable disposable = EasyMock.createMock(Disposable.class);
        Capture<Runnable> subRunCapture = EasyMock.newCapture(CaptureType.ALL);
        
        EasyMock.expect(delayWorker.schedule(EasyMock.capture(subRunCapture),
                EasyMock.eq(delay), EasyMock.same(unit)))
                .andReturn(disposable);
        EasyMock.replay(delayWorker);
        
        worker.schedule(run, delay, unit);
        List<Runnable> subRunList = subRunCapture.getValues();
        
        assertEquals(1, subRunList.size());
        Runnable subRun = subRunList.get(0);
        
        Capture<ExclusiveRunnable> exRunCapture = EasyMock.newCapture(CaptureType.ALL);
        executor.execute(EasyMock.capture(exRunCapture));
        EasyMock.replay(executor);
        
        subRun.run();

        List<ExclusiveRunnable> exRunList = exRunCapture.getValues();
        assertEquals(1, exRunList.size());
        
        ExclusiveRunnable exRun = exRunList.get(0);
        assertEquals(scope, exRun.getScope());
        assertEquals(exclusive, exRun.isExclusive());
        
        run.run();
        EasyMock.replay(run);
        
        exRun.run();
        
        EasyMock.verify(run);
    }
    
    @Test
    public void onTaskExecuteViaSchedulePeriodicallyShouldExecuteUsingExecutor() {
        long delay = 5L;
        TimeUnit unit = TimeUnit.DAYS;
        Runnable run = EasyMock.createMock(Runnable.class);
        Disposable disposable = EasyMock.createMock(Disposable.class);
        Capture<Runnable> subRunCapture = EasyMock.newCapture(CaptureType.ALL);
        
        EasyMock.expect(delayWorker.schedulePeriodically(EasyMock.capture(subRunCapture),
                EasyMock.eq(delay), EasyMock.eq(delay), EasyMock.same(unit)))
                .andReturn(disposable);
        EasyMock.replay(delayWorker);
        
        worker.schedulePeriodically(run, delay, delay, unit);
        List<Runnable> subRunList = subRunCapture.getValues();
        
        assertEquals(1, subRunList.size());
        Runnable subRun = subRunList.get(0);
        
        Capture<ExclusiveRunnable> exRunCapture = EasyMock.newCapture(CaptureType.ALL);
        executor.execute(EasyMock.capture(exRunCapture));
        EasyMock.replay(executor);
        
        subRun.run();

        List<ExclusiveRunnable> exRunList = exRunCapture.getValues();
        assertEquals(1, exRunList.size());
        
        ExclusiveRunnable exRun = exRunList.get(0);
        assertEquals(scope, exRun.getScope());
        assertEquals(exclusive, exRun.isExclusive());
        
        run.run();
        EasyMock.replay(run);
        
        exRun.run();
        
        EasyMock.verify(run);
    }
    
    @Test
    public void disposeShouldDelegateToDelayWorker() {
        delayWorker.dispose();
        EasyMock.replay(delayWorker);
        
        worker.dispose();
        
        EasyMock.verify(delayWorker);
    }
    
    @Test
    public void isDisposeShouldDelegateToDelayWorker() {
        boolean disposed = true;
        EasyMock.expect(delayWorker.isDisposed()).andReturn(disposed);
        EasyMock.replay(delayWorker);
        
        boolean result = worker.isDisposed();
        
        assertEquals(disposed, result);
        EasyMock.verify(delayWorker);
    }
    
}
