package org.drools.persistence.processinstance;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.drools.container.spring.beans.persistence.JPAKnowledgeServiceBean;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
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
		registerObjectMarshallingStrategies(ksession);	
		
		ksession.startProcess(VARIABLE_PERSISTENCE_PROCESS_ID, params);

		int sessionId = ksession.getId();
		
		return sessionId;
	}
	
	private void resumeProcess(int sessionId, Map<String, Object> params) {
		StatefulKnowledgeSession ksession = knowledgeProvider.loadStatefulKnowledgeSession(sessionId);
		
		registerWorkItemHandlers(ksession);
		registerObjectMarshallingStrategies(ksession);
		
		ksession.getWorkItemManager().completeWorkItem(variablePersisterSuspendWorkItemHandlerId, params);
		
		ksession.dispose();
	}
	
	private void registerWorkItemHandlers(StatefulKnowledgeSession ksession) {
		ksession.getWorkItemManager()
			.registerWorkItemHandler("suspendWorkItemHandler", new VariablePersisterSuspendWorkItemHandler());
		ksession.getWorkItemManager()
			.registerWorkItemHandler("checkRestoredVariablesWorkItemHandler", new CheckRestoredVariablesWorkItemHandler());
	}
	
	private void registerObjectMarshallingStrategies(StatefulKnowledgeSession ksession) {
		Environment env = ksession.getEnvironment();
		env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new ObjectMarshallingStrategy[] {
				new JPAPlaceholderResolverStrategy(env),
				MarshallerFactory.newIdentityMarshallingStrategy(
						MarshallerFactory.newClassFilterAcceptor(new String[] {"javax.persistence.Entity"})),
				MarshallerFactory.newSerializeMarshallingStrategy(
						MarshallerFactory.newClassFilterAcceptor(new String[] {"java.io.Serializable"}))
		});
		/*VariablePersistenceStrategyFactory
			.getVariablePersistenceStrategy()
				.setPersister("javax.persistence.Entity", 
						      "org.drools.persistence.processinstance.persisters.JPAVariablePersister");
		VariablePersistenceStrategyFactory
			.getVariablePersistenceStrategy()
				.setPersister("java.io.Serializable", 
						      "org.drools.persistence.processinstance.persisters.SerializableVariablePersister");*/
	}
}
