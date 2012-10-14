package org.drools.workitem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.drools.WorkItemHandlerNotFoundException;
import org.drools.container.spring.beans.persistence.JPAKnowledgeServiceBean;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.workitem.handler.SuspendWorkItemHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/conf/workitem-id-conf.xml")
public class WorkItemIdTest {
	
	private static int sessionId;
	
	@Resource(name="knowledgeProvider")
	private JPAKnowledgeServiceBean knowledgeProvider;

	@Test
	public void shouldLoadSpringContext() {
		assertNotNull( knowledgeProvider );
	}
	
	@Test
	public void shouldStartAndSuspendFlow() {
		
		StatefulKnowledgeSession ksession = knowledgeProvider.newStatefulKnowledgeSession();
		
		ksession.getWorkItemManager().registerWorkItemHandler( "suspend", new SuspendWorkItemHandler() );
		
		ksession.startProcess( "workitem-id-flow" );
		
		sessionId = ksession.getId();
	}
	
	@Test(expected=WorkItemHandlerNotFoundException.class)
	public void shouldResumeFlow() {
		
		StatefulKnowledgeSession ksession = knowledgeProvider.loadStatefulKnowledgeSession( sessionId );
		
		ksession.getWorkItemManager().registerWorkItemHandler( "suspend", new SuspendWorkItemHandler() );
		
		try {
			ksession.getWorkItemManager().completeWorkItem( 1L, null );
		} catch ( RuntimeException e ) {
		
			assertTrue( e.getCause().getMessage().contains( "Could not find work item handler for " ) );
			assertTrue( e.getCause().getCause() instanceof WorkItemHandlerNotFoundException );
			
			WorkItemHandlerNotFoundException wihnfe = ( WorkItemHandlerNotFoundException ) e.getCause().getCause();
			
			assertEquals( "noHandler", wihnfe.getWorkItemName() );
			
			throw wihnfe;
		}
	
	}
}
