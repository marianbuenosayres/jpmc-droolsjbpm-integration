package org.drools.exception.handling;

import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessNodeExceptionOccurredEvent;

/**
 * 
 * @author nicolas.loriente
 *
 */
public class MyBusinessExceptionListener extends DefaultProcessEventListener {

    @Override
    public void onNodeException(ProcessNodeExceptionOccurredEvent event) {

        if (event.getError() instanceof MyBusinessException){
            return;
        }

        throw new MyBusinessException(event);
    }
}
