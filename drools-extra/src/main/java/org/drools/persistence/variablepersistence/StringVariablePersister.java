/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.persistence.variablepersistence;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.persistence.processinstance.persisters.JPAVariablePersister;
import org.drools.persistence.processinstance.persisters.VariablePersister;
import org.drools.persistence.processinstance.variabletypes.VariableInstanceInfo;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 *
 * @author JBoss
 * @author nicolas.loriente
 *
 */

public class StringVariablePersister implements VariablePersister {

	public VariableInstanceInfo persistExternalVariable(String variableName,
														Object variable,
														VariableInstanceInfo oldValue,
														Environment env) {
		if (variable == null
				|| (oldValue != null && oldValue.getPersister().equals(""))) {
			return null;
		}

		try {
			StringPersistedVariable result = null;
			if (oldValue instanceof StringPersistedVariable) {
				result = (StringPersistedVariable) oldValue;
			}

			boolean newVariable = false;
			if (result == null) {
				result = new StringPersistedVariable();

				newVariable = true;
			}

			result.setPersister(this.getClass().getName());
			result.setName(variableName);
			result.setString((String) variable);

			persistVariable(result, env, newVariable);

			//System.out.println("Saving StringPersistedVariable id="
			//		+ result.getId() + " string=" + result.getString());

			return result;

		} catch (Throwable t) {
			Logger.getLogger(JPAVariablePersister.class.getName()).log(	Level.SEVERE, null, t);

			throw new RuntimeException("Could not persist external variable", t);
		}

	}

	public Object getExternalPersistedVariable(VariableInstanceInfo variableInstanceInfo, Environment env) {
		return (variableInstanceInfo == null)
			? null
			: ((StringPersistedVariable)variableInstanceInfo).getString();
	}

	private void persistVariable(StringPersistedVariable result, Environment env, boolean newVariable) {

        EntityManagerFactory emf = ( EntityManagerFactory ) env.get( EnvironmentName.ENTITY_MANAGER_FACTORY );
        EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager.getResource( emf );
        EntityManager em = emHolder.getEntityManager();

		if (newVariable) {
			em.persist(result);
		} else {
			em.merge(result);
		}
	}
}
