package org.krybrig.exclutor.internal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author kassle
 */
public class DummyLock implements Lock {

    @Override
    public void lock() {
        
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        
    }

    @Override
    public boolean tryLock() {
        return true;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return true;
    }

    @Override
    public void unlock() {
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
