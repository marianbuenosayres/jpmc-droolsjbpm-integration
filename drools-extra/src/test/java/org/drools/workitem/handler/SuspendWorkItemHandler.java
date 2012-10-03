package org.drools.workitem.handler;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

/**
 * 
 * @author nicolas.loriente
 *
 */
public class SuspendWorkItemHandler implements WorkItemHandler {

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		System.out.println("Suspending....");
		// do nothing and suspend
	}
	
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) { }
	
}
