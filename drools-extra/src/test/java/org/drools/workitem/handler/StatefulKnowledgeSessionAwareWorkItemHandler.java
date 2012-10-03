package org.drools.workitem.handler;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatefulKnowledgeSessionAware;
import org.drools.runtime.StatefulKnowledgeSessionAwareTest;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

/**
 * 
 * @author nicolas.loriente
 *
 */
public class StatefulKnowledgeSessionAwareWorkItemHandler implements WorkItemHandler, StatefulKnowledgeSessionAware {

	private StatefulKnowledgeSession ksession;
	
	public void setStatefulKnowledgeSession(StatefulKnowledgeSession ksession) {
		this.ksession = ksession;
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		
		StatefulKnowledgeSessionAwareTest.isKnowledgeSessionAware = ksession != null;
		
		manager.completeWorkItem(workItem.getId(), null);
		
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) { }
	
}
