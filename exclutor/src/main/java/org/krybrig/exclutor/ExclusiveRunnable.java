package org.krybrig.exclutor;

/**
 *
 * @author kassle
 */
public interface ExclusiveRunnable extends Runnable {
    
    String getScope();

    boolean isExclusive();
}
