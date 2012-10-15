package org.drools.persistence.variablepersistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.drools.event.DefaultProcessEventListener;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Persists string variables in process instances
 * @author marianbuenosayres
 *
 */
public class StringPersisterProcessEventListener extends DefaultProcessEventListener {

	@Override
	public void afterProcessStarted(ProcessStartedEvent event) {
		WorkflowProcessInstance instance = (WorkflowProcessInstance) event.getProcessInstance();
		Environment env = event.getKnowledgeRuntime().getEnvironment();
		List<StringPersistedVariable> vars = readVars(instance.getId(), env);
		for (StringPersistedVariable var : vars) {
			instance.setVariable(var.getName(), var.getString());
		}
	}
	
	@Override
	public void afterVariableChanged(ProcessVariableChangedEvent event) {
		if (event.getNewValue() instanceof String) {
			Environment env = event.getKnowledgeRuntime().getEnvironment();
			String variableName = event.getVariableId();
			long processInstanceId = event.getProcessInstance().getId();
			String newValue = String.valueOf(event.getNewValue());
			String oldValue = String.valueOf(event.getOldValue());
			persist(variableName, processInstanceId, newValue, oldValue, env);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<StringPersistedVariable> readVars(long processInstanceId, Environment env) {
		EntityManagerFactory emf = (EntityManagerFactory) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
		EntityManager em = null;
		List<StringPersistedVariable> vars = new ArrayList<StringPersistedVariable>();
		if (emf != null) {
			try {
				EntityManagerHolder holder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(emf);
				if (holder == null) {
					em = emf.createEntityManager();
				} else {
					em = holder.getEntityManager();
				}
				Query query = em.createQuery("select e" +
						" from org.drools.persistence.variablepersistence.StringPersistedVariable e" +
						" where e.processInstanceId = :processInstanceId");
				query.setParameter("processInstanceId", processInstanceId);
				vars = query.getResultList();
			} finally {
				if (em != null) {
					em.flush();
				}
			}
		}
		return vars;
	}
	
	@SuppressWarnings("unchecked")
	protected void persist(String variableName, long processInstanceId, String newValue, String oldValue, Environment env) {
		EntityManagerFactory emf = (EntityManagerFactory) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
		EntityManager em = null;
		if (emf != null) {
			try {
				EntityManagerHolder holder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(emf);
				if (holder == null) {
					em = emf.createEntityManager();
				} else {
					em = holder.getEntityManager();
				}
				Query query = em.createQuery("select e" +
						" from org.drools.persistence.variablepersistence.StringPersistedVariable e" +
						" where e.processInstanceId = :processInstanceId and e.name = :varName");
				query.setParameter("processInstanceId", processInstanceId);
				query.setParameter("varName", variableName);
				List<StringPersistedVariable> vars = query.getResultList();
				if (vars != null && !vars.isEmpty()) {
					StringPersistedVariable var = vars.iterator().next();
					if (var == null) {
						var = asStringPersistedValue(variableName, processInstanceId, newValue);
						em.persist(var);
					} else if (newValue == null) {
						em.remove(var); //newValue == null implies deletion
					} else {
						var.setString(newValue);
						em.merge(var);
					}
				} else {
					StringPersistedVariable var = asStringPersistedValue(variableName, processInstanceId, newValue);
					em.persist(var);
				}
			} finally {
				if (em != null) {
					em.flush();
				}
			}
		}
	}

	private StringPersistedVariable asStringPersistedValue(String variableName,
			long processInstanceId, String newValue) {
		StringPersistedVariable var = new StringPersistedVariable();
		var.setName(variableName);
		var.setPlaceholder(getClass().getName());
		var.setProcessInstanceId(processInstanceId);
		var.setString(newValue);
		return var;
	}
}
