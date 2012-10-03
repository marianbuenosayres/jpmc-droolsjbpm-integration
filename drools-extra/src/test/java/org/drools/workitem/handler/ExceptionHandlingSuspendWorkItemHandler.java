package org.drools.workitem.handler;

import org.drools.exception.handling.ProcessExceptionHandlerTest;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

/**
 * 
 * @author nicolas.loriente
 *
 */
public class ExceptionHandlingSuspendWorkItemHandler implements WorkItemHandler {

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		ProcessExceptionHandlerTest.exceptionHandlingSuspendWorkItemHandlerId = workItem.getId();
		
		System.out.println( "Suspending..." );
		
	}

	public void abortWorkItem( WorkItem workItem, WorkItemManager manager ) { }
	
}
