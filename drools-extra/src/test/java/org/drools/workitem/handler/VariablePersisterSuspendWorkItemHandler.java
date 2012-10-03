package org.drools.workitem.handler;

import org.drools.persistence.processinstance.ChildDummySerializable;
import org.drools.persistence.processinstance.ParentDummySerializable;
import org.drools.persistence.processinstance.VariablePersistenceStrategyTest;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

/**
 * 
 * @author nicolas.loriente
 *
 */
public class VariablePersisterSuspendWorkItemHandler implements WorkItemHandler {

	public void executeWorkItem( WorkItem workItem, WorkItemManager manager ) {
		VariablePersistenceStrategyTest.variablePersisterSuspendWorkItemHandlerId = workItem.getId();

		ParentDummySerializable parentDummySerializable = (ParentDummySerializable) 
				workItem.getParameter( "parentDummySerializable" );
		
		ChildDummySerializable childDummySerializable = (ChildDummySerializable) 
				workItem.getParameter( "childDummySerializable" );
		
		
		System.out.println( "parentDummySerializable: " + parentDummySerializable );
		System.out.println( "childDummySerializable: " + childDummySerializable );

	}
	
	public void abortWorkItem( WorkItem workItem, WorkItemManager manager ) { }

}
