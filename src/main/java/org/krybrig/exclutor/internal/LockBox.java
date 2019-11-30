package org.krybrig.exclutor.internal;

import java.util.concurrent.locks.Lock;

/**
 *
 * @author kassle
 */
interface LockBox {
    Lock getLock(String scope);
}
