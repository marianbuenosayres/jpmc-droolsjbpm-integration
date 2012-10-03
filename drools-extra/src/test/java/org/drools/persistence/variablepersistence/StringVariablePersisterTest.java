package org.drools.persistence.variablepersistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.drools.KnowledgeBaseFactory;
import org.drools.container.spring.beans.persistence.JPAKnowledgeServiceBean;
import org.drools.persistence.processinstance.variabletypes.VariableInstanceInfo;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
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
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;
	
	@Resource(name="knowledgeProvider")
	private JPAKnowledgeServiceBean knowledgeProvider;
	
	
	@Test
	public void shouldGetExternalPersistedVariable() {
		String myString = "my string";
	
		StringPersistedVariable stringPersistedVariable = new StringPersistedVariable();
		stringPersistedVariable.setString(myString);
		
		StringVariablePersister variablePersister = new StringVariablePersister();
		
		Object result = variablePersister.getExternalPersistedVariable(stringPersistedVariable, null);
		
		assertNotNull(result);
		assertTrue(result instanceof String);
		assertEquals(result.toString(), myString);
	}
	
	@Test
	public void shouldGetNullFromExternalPersistedVariable() {		
		StringVariablePersister variablePersister = new StringVariablePersister();
		
		Object result = variablePersister.getExternalPersistedVariable(null, null);
		
		assertNull(result);
	}
	
	@Test
	public void shouldReturnNullFromPersistExternalVariable() {
		String variableName = "myString";
		String variableValue = "my string";
		
		StringVariablePersister variablePersister = new StringVariablePersister();
		
		VariableInstanceInfo result = variablePersister.persistExternalVariable(variableName, null, null, null);
		
		assertNull(result);
		
		VariableInstanceInfo oldValue = new VariableInstanceInfo();
		oldValue.setPersister("");
		
		result = variablePersister.persistExternalVariable(variableName, variableValue, oldValue, null);
		
		assertNull(result);
	}
	
	@Test(expected=RuntimeException.class)
	public void shouldTrhowExceptionFromPersistExternalVariable() {
		
		StringVariablePersister variablePersister = new StringVariablePersister();
		Object variableName = new Object();
		String variableValue = "my string";
			
		VariableInstanceInfo persistedVariable =  null;
		
		try {
			persistedVariable = variablePersister.persistExternalVariable((String)variableName, variableValue, null, null);
		} catch (RuntimeException e) {
			assertNull(persistedVariable);
			assertTrue(e instanceof ClassCastException);
			assertEquals("java.lang.Object cannot be cast to java.lang.String", e.getMessage());
			assertEquals("Could not persist external variable", e.getCause().getMessage());
			
			throw e;
		}
		
	}
	
	@Test
	@Transactional
	public void shouldPersistVariable() {
		String variableName = "myString";
		String variableValue = "my string";
		
		Environment environment = KnowledgeBaseFactory.newEnvironment();
		environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, entityManagerFactory);
		
		StringVariablePersister variablePersister = new StringVariablePersister();
		
		VariableInstanceInfo persistedVariable = variablePersister.persistExternalVariable(variableName, variableValue, null, environment);
		
		assertNotNull(persistedVariable.getId());
		
		VariableInstanceInfo persistedVariableFromDb = getPersistedVariable(variableName);
		
		assertTrue(persistedVariableFromDb instanceof StringPersistedVariable);
		assertEquals(variableValue, ((StringPersistedVariable)persistedVariableFromDb).getString());
	}
	
	@Test
	@Transactional
	public void shouldUpdatedPersistedVariable() {
		String variableName = "myString";
		String variableValue = "my string";
		
		Environment environment = KnowledgeBaseFactory.newEnvironment();
		environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, entityManagerFactory);
		
		StringVariablePersister variablePersister = new StringVariablePersister();
		
		VariableInstanceInfo persistedVariable = variablePersister.persistExternalVariable(variableName, variableValue, null, environment);
		
		assertNotNull(persistedVariable.getId());
		
		VariableInstanceInfo persistedVariableFromDb = getPersistedVariable(variableName);
		
		assertTrue(persistedVariableFromDb instanceof StringPersistedVariable);
		assertEquals(variableValue, ((StringPersistedVariable)persistedVariableFromDb).getString());
		
		String variableNewValue = "my new string";
		persistedVariable = variablePersister.persistExternalVariable(variableName, variableNewValue, persistedVariableFromDb, environment);
		
		persistedVariableFromDb = getPersistedVariable(variableName);
		
		assertTrue(persistedVariableFromDb instanceof StringPersistedVariable);
		assertEquals(variableNewValue, ((StringPersistedVariable)persistedVariableFromDb).getString());
		
	}
	
	@Test
	public void shouldPersistOriginalStringFromProcessContext() {
		String variableName = "myFlowString";
		String variableValue = "my flow string";
		
		StatefulKnowledgeSession ksession = knowledgeProvider.newStatefulKnowledgeSession();
		
		sessionId = ksession.getId();
		
		registerWorkItemHandlers(ksession);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(variableName, variableValue);
		
		ksession.startProcess(STRING_PERSISTER_PROCESS_ID, params);
		
		StringPersistedVariable persistedVariableFromDb = (StringPersistedVariable) getPersistedVariable(variableName);
				
		assertEquals(variableValue, persistedVariableFromDb.getString());
		
	}
	
	@Test
	public void shouldPersistModifiedStringFromProcessContext() {
		String variableName = "myFlowString";
		String variableValue = "my NEW flow string";
		
		StatefulKnowledgeSession ksession = knowledgeProvider.loadStatefulKnowledgeSession(sessionId);
		
		registerWorkItemHandlers(ksession);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(variableName, variableValue);
		
		ksession.getWorkItemManager().completeWorkItem(1L, params);
		
		StringPersistedVariable persistedVariableFromDb = (StringPersistedVariable) getPersistedVariable(variableName);
		
		assertEquals(variableValue, persistedVariableFromDb.getString());
		
	}
	
	private void registerWorkItemHandlers(StatefulKnowledgeSession ksession) {
		ksession.getWorkItemManager().registerWorkItemHandler("suspend", new SuspendWorkItemHandler());
	}
	
	private VariableInstanceInfo getPersistedVariable(String variableName) {
		VariableInstanceInfo persistedVariableFromDb = (VariableInstanceInfo) entityManager.createQuery(
				"SELECT vii " +
				"FROM VariableInstanceInfo vii " +
				"WHERE vii.name = '" + variableName + "'").getSingleResult();
		
		return persistedVariableFromDb;
	}
}
