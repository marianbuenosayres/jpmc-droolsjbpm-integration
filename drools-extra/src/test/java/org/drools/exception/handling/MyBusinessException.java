package org.drools.exception.handling;

import org.drools.event.process.ProcessNodeExceptionOccurredEvent;
import org.drools.workflow.instance.impl.ProcessNodeExecutionException;

/**
 * 
 * @author nicolas.loriente
 *
 */
public class MyBusinessException extends ProcessNodeExecutionException {

    private static final long serialVersionUID = 1306839952415367804L;
    private ProcessNodeExceptionOccurredEvent error;
    private String processName;
    private String nodeName;
    private String message;
    
    public MyBusinessException(ProcessNodeExceptionOccurredEvent error) {
        super(error.getError());
        this.error = error;
        this.processName = error.getProcessInstance().getProcessName();
        this.nodeName = error.getNodeInstance().getNodeName();
    }
    
    public MyBusinessException(Throwable t, String processName, String nodeName) {
    	super(t);
    	this.processName = processName;
    	this.nodeName = nodeName;
    	this.message = t.getMessage();
    }

    public String getProcessName() {
        return this.processName;
    }

    public String getNodeName() {
        return this.nodeName;
    }

    public Throwable getOriginalException() {
        return error.getError();
    }
    
    @Override
    public String getMessage() {
    	String message = "Exception: ";
    	
    	if ( error != null ) {
    		message += "[" + getOriginalException() + "] ";
    	}
    	
    	message += "occurred in process: [" +
    			getProcessName() + "] in node: [" + getNodeName() + "]. Message: " + this.message;
    	
    	return message;
    }
}
