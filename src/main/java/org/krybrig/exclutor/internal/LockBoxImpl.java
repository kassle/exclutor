package org.krybrig.exclutor.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author kassle
 */
public class LockBoxImpl implements LockBox {
    private final Map<String, Lock> lockMap = new HashMap<>();

    @Override
    public synchronized Lock getLock(String scope) {
        Lock lock = lockMap.get(scope);
        if (null == lock) {
            lock = new ReentrantLock();
            lockMap.put(scope, lock);
        }
        return lock;
    }
}
