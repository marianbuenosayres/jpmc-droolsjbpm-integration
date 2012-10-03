package org.drools.persistence.processinstance;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.drools.container.spring.beans.persistence.JPAKnowledgeServiceBean;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.workitem.handler.CheckRestoredVariablesWorkItemHandler;
import org.drools.workitem.handler.VariablePersisterSuspendWorkItemHandler;
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
@ContextConfiguration("classpath:META-INF/conf/serializable-variable-persistence-conf.xml")
@Transactional
@TransactionConfiguration(defaultRollback=true)
public class VariablePersistenceStrategyTest {
	
	public static long variablePersisterSuspendWorkItemHandlerId;
	public static ParentDummySerializable parentDummySerializable;
	public static ChildDummySerializable childDummySerializable;
	private static final String VARIABLE_PERSISTENCE_PROCESS_ID = "serializable-variable-persister-flow";
	
	@Resource(name="knowledgeProvider")
	private JPAKnowledgeServiceBean knowledgeProvider;

	
	@Test
	public void shouldLoadSpringConetxt() { }
	
	@Test
	public void shouldStartAndSuspendFlow() {
		ParentDummySerializable parentDummySerializable = new ParentDummySerializable();
		ChildDummySerializable childDummySerializable = new ChildDummySerializable();
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("parentDummySerializable", parentDummySerializable);
		params.put("childDummySerializable", childDummySerializable);
		
		int sessionId = startProcess(params);
		
		resumeProcess(sessionId, null);
		
		assertNotNull("parentDummySerializable should NOT be null", parentDummySerializable);
		assertNotNull("childDummySerializable should NOT be null", childDummySerializable);
	}

	private int startProcess(Map<String, Object> params) {
		StatefulKnowledgeSession ksession = knowledgeProvider.newStatefulKnowledgeSession();	
		
		registerWorkItemHandlers(ksession);
		registerVariablePersisters(ksession);	
		
		ksession.startProcess(VARIABLE_PERSISTENCE_PROCESS_ID, params);

		ksession.dispose();
		
		return ksession.getId();
	}
	
	private void resumeProcess(int sessionId, Map<String, Object> params) {
		StatefulKnowledgeSession ksession = knowledgeProvider.loadStatefulKnowledgeSession(sessionId);
		
		registerWorkItemHandlers(ksession);
		registerVariablePersisters(ksession);
		
		ksession.getWorkItemManager().completeWorkItem(variablePersisterSuspendWorkItemHandlerId, params);
		
		ksession.dispose();
	}
	
	private void registerWorkItemHandlers(StatefulKnowledgeSession ksession) {
		ksession.getWorkItemManager()
			.registerWorkItemHandler("suspendWorkItemHandler", new VariablePersisterSuspendWorkItemHandler());
		ksession.getWorkItemManager()
			.registerWorkItemHandler("checkRestoredVariablesWorkItemHandler", new CheckRestoredVariablesWorkItemHandler());
	}
	
	private void registerVariablePersisters(StatefulKnowledgeSession ksession) {
		VariablePersistenceStrategyFactory
			.getVariablePersistenceStrategy()
				.setPersister("javax.persistence.Entity", 
						      "org.drools.persistence.processinstance.persisters.JPAVariablePersister");
		VariablePersistenceStrategyFactory
			.getVariablePersistenceStrategy()
				.setPersister("java.io.Serializable", 
						      "org.drools.persistence.processinstance.persisters.SerializableVariablePersister");
	}
}
