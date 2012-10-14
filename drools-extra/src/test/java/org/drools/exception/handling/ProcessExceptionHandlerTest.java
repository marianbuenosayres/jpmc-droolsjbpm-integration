package org.drools.exception.handling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.drools.container.spring.beans.persistence.JPAKnowledgeServiceBean;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.workitem.handler.ExceptionHandlingSuspendWorkItemHandler;
import org.drools.workitem.handler.ThrowExceptionWorkItemHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author nicolas.loriente
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/conf/exception-handling-conf.xml")
@Transactional
@TransactionConfiguration(defaultRollback=true)
public class ProcessExceptionHandlerTest {
	
	public static long exceptionHandlingSuspendWorkItemHandlerId;
	private static final String EXCEPTION_PROCESS_ID = "exception-flow";
	
	@Resource(name="knowledgeProvider")
	private JPAKnowledgeServiceBean knowledgeProvider;

	@Test
	public void shouldLoadSpringContext() { }
	
	
	@Test(expected=MyBusinessException.class)
	public void shouldThrowExceptionFromActionNode() throws Throwable {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "shouldThrowExceptionInAction", true );
		
		try {
			startProcess(EXCEPTION_PROCESS_ID, params);
		} catch ( Throwable t ) {	
			assertTrue( t.getCause() instanceof Exception );
			assertTrue( t instanceof MyBusinessException );
			assertTrue( t.getMessage().contains( "unable to execute Action" ) );
			
			MyBusinessException mbe = ( MyBusinessException ) t;
			
			assertEquals( "Exception Flow", mbe.getProcessName() );
			assertEquals( "Throw Exception?", mbe.getNodeName() );
			
			throw t;
		}
	}
	
	@Test(expected=MyBusinessException.class)
	public void shouldThrowExceptionFromWorkItemInSubflow() throws Throwable {
		int sessionId = startProcess(EXCEPTION_PROCESS_ID, null);
		
		try {
			resumeProcess(sessionId, exceptionHandlingSuspendWorkItemHandlerId);
		} catch ( Throwable t ) {
			assertTrue( t.getCause() instanceof Exception );
			assertTrue( t instanceof MyBusinessException );
			assertTrue( t.getMessage().contains( "My Error" ) );
			
			MyBusinessException mbe = ( MyBusinessException ) t;
			
			assertEquals( "Exception Subflow", mbe.getProcessName() );
			assertEquals( "Throw Exception Node", mbe.getNodeName() );
			
			throw t;
		}
	}
	
	@Test(expected=MyBusinessException.class)
	public void shouldThrowExceptionFromRuleset() throws Throwable {
		try {
			runRuleFlow();
		} catch ( Throwable t ) {
			assertTrue( t.getCause() instanceof RuntimeException );
			assertTrue( t instanceof MyBusinessException );
			assertEquals( "Exception: occurred in process: [Exception Rule Flow] in node: [RuleSet]. Message: ABC", 
						  t.getMessage() );
			
			MyBusinessException mbe = ( MyBusinessException ) t;
			
			assertEquals( "Exception Rule Flow", mbe.getProcessName() );
			assertEquals( "RuleSet", mbe.getNodeName() );
			
			throw t;
		}
		
	}
	
	private int startProcess(String processName, Map<String, Object> params) {
		StatefulKnowledgeSession ksession = knowledgeProvider.newStatefulKnowledgeSession();

		registerExceptionHandler(ksession);
		registerWorkItemHandlers(ksession);
		
		ksession.startProcess(processName, params);
		
		return ksession.getId();
	}
	
	private void resumeProcess(int sessionId, long workItemId) {
		StatefulKnowledgeSession ksession = knowledgeProvider.loadStatefulKnowledgeSession(sessionId);
		
		registerExceptionHandler(ksession);
		registerWorkItemHandlers(ksession);
			
		ksession.getWorkItemManager().completeWorkItem(workItemId, null);
	}
	
	private void runRuleFlow() {
		StatefulKnowledgeSession ksession = knowledgeProvider.newStatefulKnowledgeSession();
		
		ksession.insert("one");
		
		ksession.startProcess("exception-rule-flow", null);

		ksession.fireAllRules();
	}
	
	private void registerExceptionHandler(StatefulKnowledgeSession ksession) {
		ksession.addEventListener(new MyBusinessExceptionListener());
	}
	
	private void registerWorkItemHandlers(StatefulKnowledgeSession ksession) {
		ksession.getWorkItemManager().registerWorkItemHandler("throwException", new ThrowExceptionWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("suspend", new ExceptionHandlingSuspendWorkItemHandler());
	}
}
