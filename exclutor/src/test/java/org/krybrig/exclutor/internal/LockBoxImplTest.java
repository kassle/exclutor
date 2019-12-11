package org.krybrig.exclutor.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kassle
 */
public class LockBoxImplTest {
    
    private LockBox lockBox;
    
    @Before
    public void setUp() {
        lockBox = new LockBoxImpl();
    }
    
    @Test
    public void getLockShouldCreateNewLockObjectWhenForTheFirstTime() {
        String scope = "table.users";
        Lock lock = lockBox.getLock(scope);
        
        assertNotNull(lock);
    }
    
    @Test
    public void getLockShouldReturnSameLockObjectWhenScopeIsEqual() {
        String scope = "table.users";
        Lock lock1 = lockBox.getLock(scope);
        Lock lock2 = lockBox.getLock(scope);
        
        assertSame(lock1, lock2);
    }
    
    @Test
    public void getLockShouldReturnDifferentLockObjectWhenScopeIsDifferent() {
        String scope1 = "table.users";
        String scope2 = "table.profiles";
        
        Lock lock1 = lockBox.getLock(scope1);
        Lock lock2 = lockBox.getLock(scope2);
        
        assertNotSame(lock1, lock2);
    }
    
    @Test
    public void getLockShouldBeThreadSafe() throws InterruptedException, ConcurrentModificationException  {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        final List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        
        int run = 1000;
        int uniq = 100;
        
        for (int i = 0; i < run; i++) {
            final int lockId = i % uniq;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    String scope = "scope-" + lockId;
                    Lock lock = lockBox.getLock(scope);
                    list.add(lock.hashCode());
                }
            });
        }
        
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(run, list.size());
        assertEquals(uniq, new HashSet(list).size());
    }
}
