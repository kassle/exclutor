package org.krybrig.exclutor;

/**
 *
 * @author kassle
 */
public abstract class AbstractExclusiveRunnable implements ExclusiveRunnable {
    private final String scope;
    private final boolean exclusive;

    public AbstractExclusiveRunnable(String scope, boolean exclusive) {
        this.scope = scope;
        this.exclusive = exclusive;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public boolean isExclusive() {
        return exclusive;
    }
    
    
}
