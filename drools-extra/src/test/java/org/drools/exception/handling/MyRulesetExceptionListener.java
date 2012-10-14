package org.drools.exception.handling;

import java.util.Map.Entry; 

import org.drools.common.InternalAgenda;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.WorkingMemory;
import org.drools.runtime.rule.impl.DefaultConsequenceExceptionHandler;

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
                WorkflowProcessInstance processInstance = ((WorkflowProcessInstance)ksession.getProcessInstance(entry.getKey()));
                NodeInstance nodeInstance = processInstance.getNodeInstance(Long.parseLong(entry.getValue()));

                throw new MyBusinessException(exception, processInstance.getProcessName(), nodeInstance.getNodeName());
            }
        }
        else{
            super.handleException(activation, workingMemory, exception);
        }
    }

}