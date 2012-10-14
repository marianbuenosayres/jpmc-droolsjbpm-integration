package org.drools.runtime;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.annotation.Resource;

import org.drools.container.spring.beans.persistence.JPAKnowledgeServiceBean;
import org.drools.workitem.handler.StatefulKnowledgeSessionAwareWorkItemHandler;
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
@ContextConfiguration("classpath:META-INF/conf/session-aware-conf.xml")
@Transactional
@TransactionConfiguration(defaultRollback=true)
public class StatefulKnowledgeSessionAwareTest {

	public static boolean isKnowledgeSessionAware;
	
	@Resource(name="knowledgeProvider")
	private JPAKnowledgeServiceBean knowledgeProvider;
	
	
	@Test
	public void shouldLoadSpringContext(){ }
	
	@Test
	public void shouldBeKnowledgeSessionAware() {
		
		startProcess(null);
		
		assertTrue(isKnowledgeSessionAware);
		
	}
	
	private int startProcess(Map<String, Object> params) {
		StatefulKnowledgeSession ksession = knowledgeProvider.newStatefulKnowledgeSession();
		
		StatefulKnowledgeSessionAwareWorkItemHandler sessionAwareWorkItemHandler = 
				new StatefulKnowledgeSessionAwareWorkItemHandler();
		sessionAwareWorkItemHandler.setStatefulKnowledgeSession(ksession);
		
		ksession.getWorkItemManager().registerWorkItemHandler(
				"sessionAwareWorkItemHandler", sessionAwareWorkItemHandler);
		
		ksession.startProcess("session-aware-flow", params);
		
		int sessionId = ksession.getId();
		
		ksession.dispose();
		
		return sessionId;
	}
	
}
