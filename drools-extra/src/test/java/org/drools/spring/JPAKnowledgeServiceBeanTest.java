package org.drools.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.drools.container.spring.beans.persistence.JPAKnowledgeServiceBean;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.workitem.handler.SuspendWorkItemHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author nicolas.loriente
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/conf/spring-servicebean-conf.xml")
public class JPAKnowledgeServiceBeanTest {

	public static int sessionId;
	public static long processInstanceId;
	
	@Autowired
	private JPAKnowledgeServiceBean knowledgeServiceBean;
	
	
	@Test
	public void shouldLoadSpringContext() { }
	
	@Test
	public void shouldCreateNewKnowledgeSession() {
		StatefulKnowledgeSession ksession = knowledgeServiceBean.newStatefulKnowledgeSession();
		
		assertNotNull(ksession);
		
		sessionId = ksession.getId();
		
		registerWorkItemHandlers(ksession);
		
		ProcessInstance processInstance = ksession.startProcess("spring-servicebean-flow");
		
		ksession.dispose();
		
		assertNotNull(processInstance);
		
		processInstanceId = processInstance.getId();
		
		assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
	}
	
	@Test
	public void shouldLoadExistingKnowledgeSession() {
		StatefulKnowledgeSession ksession = knowledgeServiceBean.loadStatefulKnowledgeSession(sessionId);
		
		assertNotNull(ksession);
		
		ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
		
		assertNotNull(processInstance);
		
		registerWorkItemHandlers(ksession);
		
		ksession.getWorkItemManager().completeWorkItem(1L, null);
		
		ksession.dispose();
		
		assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
	}
	
	private void registerWorkItemHandlers(StatefulKnowledgeSession ksession) {
		ksession.getWorkItemManager().registerWorkItemHandler("suspend", new SuspendWorkItemHandler());
	}
}
