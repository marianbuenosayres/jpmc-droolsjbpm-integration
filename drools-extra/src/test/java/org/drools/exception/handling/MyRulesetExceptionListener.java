package org.drools.exception.handling;

import java.util.Map.Entry;

import org.drools.common.InternalAgenda;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.WorkingMemory;
import org.drools.runtime.rule.impl.DefaultConsequenceExceptionHandler;
import org.drools.workflow.instance.NodeInstance;

/**
 * 
 * @author nicolas.loriente
 *
 */
public class MyRulesetExceptionListener extends DefaultConsequenceExceptionHandler{

    @Override
    public void handleException(Activation activation, WorkingMemory workingMemory, Exception exception) {
        String ruleFlowGroupName = ((org.drools.rule.Rule)activation.getRule()).getRuleFlowGroup();
        if ( ruleFlowGroupName != null && !ruleFlowGroupName.isEmpty()){

            StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl) workingMemory;
            InternalAgenda internalAgenda = (InternalAgenda)ksession.getInternalWorkingMemory().getAgenda();

            org.drools.common.RuleFlowGroupImpl ruleFlowGroup = (org.drools.common.RuleFlowGroupImpl) internalAgenda.getRuleFlowGroup(ruleFlowGroupName);

            for (Entry<Long, String> entry : ruleFlowGroup.getNodeInstances().entrySet()) {
                RuleFlowProcessInstance processInstance = ((RuleFlowProcessInstance)ksession.getProcessInstance(entry.getKey()));
                NodeInstance nodeInstance = processInstance.getNodeInstance(Long.parseLong(entry.getValue()));

                throw new MyBusinessException(exception, processInstance.getProcessName(), nodeInstance.getNodeName());
            }
        }
        else{
            super.handleException(activation, workingMemory, exception);
        }
    }

}