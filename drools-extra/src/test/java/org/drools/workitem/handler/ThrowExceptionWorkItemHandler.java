package org.drools.workitem.handler;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

/**
 * 
 * @author nicolas.loriente
 *
 */
public class ThrowExceptionWorkItemHandler implements WorkItemHandler {

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		System.out.println("Throwing Error");
		throw new Error( "My Error" );
	}

	public void abortWorkItem( WorkItem workItem, WorkItemManager manager ) {	}
	
}
