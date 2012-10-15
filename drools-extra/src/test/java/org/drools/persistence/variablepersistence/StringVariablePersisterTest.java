package org.drools.persistence.variablepersistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.drools.KnowledgeBaseFactory;
import org.drools.container.spring.beans.persistence.JPAKnowledgeServiceBean;
import org.drools.event.process.ProcessEventListener;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.workitem.handler.SuspendWorkItemHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author nicolas.loriente
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/conf/string-variable-persiter-conf.xml")
public class StringVariablePersisterTest {
	
	public static int sessionId;
	private static final String STRING_PERSISTER_PROCESS_ID = "string-variable-persister-flow";
	
	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;
	
	@Resource(name="knowledgeProvider")
	private JPAKnowledgeServiceBean knowledgeProvider;
	
	
	@Test @Transactional
	public void shouldGetPersistedVariable() {
		String myString = "my string";
	
		StringPersistedVariable stringPersistedVariable = new StringPersistedVariable();
		stringPersistedVariable.setString(myString);
		
		StringPersisterProcessEventListener listener = new StringPersisterProcessEventListener();
		
		Environment env = KnowledgeBaseFactory.newEnvironment();
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, entityManagerFactory);
		
		listener.persist("varName", 1, myString, null, env);
		List<StringPersistedVariable> vars = listener.readVars(1, env);
		
		assertNotNull(vars);
		assertFalse(vars.isEmpty());
		assertEquals(vars.iterator().next().getString(), myString);
	}
	
	@Test @Transactional
	public void shouldGetNullFromExternalPersistedVariable() {		
		String myString = "my string";
		
		StringPersistedVariable stringPersistedVariable = new StringPersistedVariable();
		stringPersistedVariable.setString(myString);
		
		StringPersisterProcessEventListener listener = new StringPersisterProcessEventListener();
		
		Environment env = KnowledgeBaseFactory.newEnvironment();
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, entityManagerFactory);
		
		List<StringPersistedVariable> vars = listener.readVars(1, env);
		
		assertNotNull(vars);
		assertTrue(vars.isEmpty());
	}
	
	@Test @Transactional
	public void shouldSaveValueAfterStartProcess() {
		
		String variableName = "myFlowString";
		String variableValue = "my string";
		String otherVariableName = "myNonPersistentLong";
		
		StatefulKnowledgeSession ksession = knowledgeProvider.newStatefulKnowledgeSession();
		ksession.getWorkItemManager().registerWorkItemHandler("suspend", new SuspendWorkItemHandler());

		StringPersisterProcessEventListener myListener = null;
		for (ProcessEventListener listener : ksession.getProcessEventListeners()) {
			if (listener instanceof StringPersisterProcessEventListener) {
				myListener = (StringPersisterProcessEventListener) listener;
				break;
			}
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(variableName, variableValue);
		params.put(otherVariableName, 1l); //shouldn't save the long value
		
		ProcessInstance pI = ksession.startProcess(STRING_PERSISTER_PROCESS_ID, params);
		
		List<StringPersistedVariable> vars = myListener.readVars(pI.getId(), ksession.getEnvironment());
		
		assertNotNull(vars);
		assertFalse(vars.isEmpty());
		assertEquals(vars.size(), 1); //shouldn't have saved the long value
		
		StringPersistedVariable var = vars.iterator().next();
		assertEquals(var.getName(), variableName);
		assertEquals(var.getString(), variableValue);
		
	}
}
