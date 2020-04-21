package org.krybrig.exclutor.internal;

import org.krybrig.exclutor.ExclusiveRunnable;

/**
 *
 * @author kassle
 */
public class ExclusiveRunnableFuture<V> extends RunnableFuture<V> implements ExclusiveRunnable {
    private final ExclusiveRunnable delegate;

    public ExclusiveRunnableFuture(ExclusiveRunnable delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public String getScope() {
        return delegate.getScope();
    }

    @Override
    public boolean isExclusive() {
        return delegate.isExclusive();
    }    
}
