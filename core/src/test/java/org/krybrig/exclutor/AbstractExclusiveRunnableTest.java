package org.krybrig.exclutor;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kassle
 */
public class AbstractExclusiveRunnableTest {
    private static final String SCOPE = "socket.users";
    private static final boolean EXCLUSIVE = true;
    private RunnableImpl runnable;
    
    @Before
    public void setUp() {
        runnable = new RunnableImpl(SCOPE, EXCLUSIVE);
    }

    @Test
    public void getScopeShouldReturnConstructorPassedScope() {
        assertEquals(SCOPE, runnable.getScope());
    }
    
    @Test
    public void isExclusiveShouldReturnConstructorPassedExclusive() {
        assertEquals(EXCLUSIVE, runnable.isExclusive());
    }
 
    private static class RunnableImpl extends AbstractExclusiveRunnable {

        public RunnableImpl(String scope, boolean exclusive) {
            super(scope, exclusive);
        }

        @Override
        public void run() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}
