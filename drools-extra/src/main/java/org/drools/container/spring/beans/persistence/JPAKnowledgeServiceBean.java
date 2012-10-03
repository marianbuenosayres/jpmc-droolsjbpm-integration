package org.drools.container.spring.beans.persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.persistence.jpa.KnowledgeStoreService;
import org.jbpm.persistence.processinstance.VariablePersistenceStrategyFactory;
import org.jbpm.persistence.processinstance.persisters.VariablePersister;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.Map;

/**
 * <p> Spring bean that serves as a facade into Drools Session creation and reloading using persistent (JPA) 
 *     knowledge state.</p>
 *    
 * <p> Public client APIs are {@link #newStatefulKnowledgeSession( )} and 
 *     {@link #loadStatefulKnowledgeSession( int )} which do just that:<br/><br/>
 *     {@link #newStatefulKnowledgeSession( )}:  
 *     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *                Creates a new {@link org.drools.runtime.StatefulKnowledgeSession}<br/>
 *
 *     {@link #loadStatefulKnowledgeSession( int )}:
 *     &nbsp;
 *                Reloads an existing ( previously persisted {@link org.drools.runtime.StatefulKnowledgeSession} ) by the sesssionID
 *     </p>
 *    
 * <p> Here is an example on how this bean can be configured: </p>
 * 
 * <p><pre>
 * 	&ltbean id="abstractKnowledgeProvider"
 *               class="org.drools.container.spring.beans.persistence.JPAKnowledgeServiceBean"&gt;
 *               &ltproperty name="entityManagerFactory" ref="entityManagerFactory" /&gt;
 *               &ltproperty name="transactionManager" ref="txManager" /&gt;
 *               &ltproperty name="knowledgeStore" ref="kstore"/&gt;
 *  
 *               &ltproperty name="kbase"&gt;
 *                   &ltdrools:kbase id="kbase" node="node"&gt;
 *                       &ltdrools:resources&gt;
 *                           &ltdrools:resource type="DRF"
 *                                            source="classpath:./META-INF/flow/subprocess-flow.rf" /&gt;					             
 *                           &ltdrools:resource type="DRF"
 *                                            source="classpath:./META-INF/flow/parallel-subprocess-flow.rf" /&gt;
 *                           &ltdrools:resource type="DRF"
 *                                            source="classpath:./META-INF/flow/approval-flow.rf" /&gt;
 *                       &lt/drools:resources&gt;
 *                   &lt/drools:kbase&gt;
 *               &lt/property&gt;
 *               &ltproperty name="variablePersisters"&gt;
 *                   &ltutil:map&gt;
 *                       &ltentry key="javax.persistence.Entity"
 *                              value="org.drools.persistence.processinstance.persisters.JPAVariablePersister" /&gt;
 *                       &ltentry key="java.io.Serializable"
 *                              value="org.drools.persistence.processinstance.persisters.SerializableVariablePersister" /&gt;
 *                       &ltentry key="java.lang.String"
 *                              value="org.drools.persistence.processinstance.persisters.StringVariablePersister" /&gt;
 *                   &lt/util:map&gt;
 *               &lt/property&gt;
 *	&lt/bean&gt;
 *</pre></p>
 * 
 * <p> Besides "variablePersisters" all properties are mandatory ( cannot be NULL / not set ). </p> 
 * 
 * @author anatoly.polinsky
 * @author nicolas.loriente
 * 
 */
public class JPAKnowledgeServiceBean implements InitializingBean {

	private KnowledgeBase kbase;
	private KnowledgeStoreService knowledgeStore;
	private Environment environment;
	private EntityManagerFactory entityManagerFactory;
	private AbstractPlatformTransactionManager transactionManager;
	private Map<Class<?>, Class<? extends VariablePersister>> variablePersisters = Collections.emptyMap();

	public StatefulKnowledgeSession newStatefulKnowledgeSession() {
		createEnvironemnt();
		return knowledgeStore.newStatefulKnowledgeSession( this.kbase, null, this.environment );
	}
	
	public StatefulKnowledgeSession loadStatefulKnowledgeSession( final int sessionId ) {
		createEnvironemnt();	
		return knowledgeStore.loadStatefulKnowledgeSession( sessionId, kbase, null, environment );
	}

	public void afterPropertiesSet() throws Exception {
		
		if ( kbase == null ) {				
				throw new InvalidPropertyException( this.getClass(), "kbase", "Cannot be NULL" );
		}
		if ( transactionManager == null ) {				
			throw new InvalidPropertyException( this.getClass(), "transactionManager", "Cannot be NULL" );
		}
		if ( entityManagerFactory == null ) {				
			throw new InvalidPropertyException( this.getClass(), "entityManagerFactory", "Cannot be NULL" );
		}
		if ( knowledgeStore == null ) {				
			throw new InvalidPropertyException( this.getClass(), "knowledgeStore", "Cannot be NULL" );
		}
		
		// populating Variable Persisters if any are provided
		if ( variablePersisters != null && !variablePersisters.isEmpty() ) {
			for ( Map.Entry<Class<?>, Class<? extends VariablePersister>> entry : variablePersisters.entrySet() ) {
				 VariablePersistenceStrategyFactory.getVariablePersistenceStrategy().setPersister( 
						 entry.getKey().getName(), entry.getValue().getName() );
			}
		}
	}
		
	private void createEnvironemnt() {
		this.environment = KnowledgeBaseFactory.newEnvironment();
		this.environment.set( EnvironmentName.ENTITY_MANAGER_FACTORY, this.entityManagerFactory );
		this.environment.set( EnvironmentName.TRANSACTION_MANAGER, this.transactionManager );
		this.environment.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );		
	}
	
	// public accessors
	public KnowledgeBase getKbase() {
		return kbase;
	}

	public void setKbase(KnowledgeBase kbase) {
		this.kbase = kbase;
	}

	public AbstractPlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(AbstractPlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setVariablePersisters(Map<Class<?>, Class<? extends VariablePersister>> variablePersisters) {
		this.variablePersisters = variablePersisters;
	}

	public Map<Class<?>, Class<? extends VariablePersister>> getVariablePersisters() {
		return variablePersisters;
	}
	public KnowledgeStoreService getKnowledgeStore() {
		return knowledgeStore;
	}

	public void setKnowledgeStore( KnowledgeStoreService knowledgeStoreService ) {
		this.knowledgeStore = knowledgeStoreService;
	}
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}
}

