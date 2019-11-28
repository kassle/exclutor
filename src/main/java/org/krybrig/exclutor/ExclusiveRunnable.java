package org.krybrig.exclutor;

/**
 *
 * @author kassle
 */
public interface ExclusiveRunnable extends Runnable {
    
    abstract public String getScope();

    boolean isExclusive();
}
