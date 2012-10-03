package org.drools.runtime;

/**
 * <p>Interface to be implemented by any object that wishes to be notified of the {@link org.drools.runtime.StatefulKnowledgeSession}
 *    that it runs in.</p>
 *
 * <p>Implementing this interface makes sense for example when an object requires access to session artifacts/details
 *    such as rule facts, sessionID, etc.</p>
 *
 * <p>This interface can be implemented by a custom {@link org.drools.process.instance.WorkItemHandler} in case,
 *    for example, it needs to access a session to insert / retract some facts before proceeding further to the
 *    process node that fires rules ( groups, agendas, etc.. ).</p>
 *
 * @author anatoly.polinsky
 */
public interface StatefulKnowledgeSessionAware {

    /**
     * <p>Set the {@link org.drools.runtime.StatefulKnowledgeSession} that this object runs in. Normally this call will be used to
     *    initialize  the object.</p>
     * 
     * @param ksession the StatefulKnowledgeSession object to be used by this object
     */
    public void setStatefulKnowledgeSession( StatefulKnowledgeSession ksession );
}
