/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.fluent.session.impl;

import java.util.Map;
import org.drools.command.GetVariableCommand;
import org.drools.command.SetVariableCommandFromLastReturn;
import org.drools.command.runtime.SetGlobalCommand;
import org.drools.command.runtime.process.CreateProcessInstanceCommand;
import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.command.runtime.process.StartProcessCommand;
import org.drools.command.runtime.process.StartProcessInstanceCommand;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.command.runtime.rule.RetractCommand;
import org.drools.command.runtime.rule.UpdateCommand;
import org.drools.fluent.InternalSimulation;
import org.drools.fluent.session.StatefulKnowledgeSessionFluent;
import org.drools.fluent.simulation.impl.DefaultSimulationStepFluent;
import org.drools.fluent.simulation.SimulationStepFluent;
import org.drools.fluent.test.impl.AbstractFluentTest;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class DefaultStatefulKnowledgeSessionFluent extends AbstractFluentTest<StatefulKnowledgeSessionFluent>
        implements StatefulKnowledgeSessionFluent {
    
    private DefaultSimulationStepFluent step;
    
    public DefaultStatefulKnowledgeSessionFluent(InternalSimulation sim,
                                                 DefaultSimulationStepFluent step) {
        super();
        setSim( sim );
        this.step = step;
    }


    public StatefulKnowledgeSessionFluent setGlobal(String identifier,
                                                            Object object) {
        step.addCommand( new SetGlobalCommand( identifier, object ) );
        return this;
    }    
    
    public StatefulKnowledgeSessionFluent insert(Object object) {
        step.addCommand( new InsertObjectCommand( object ) );
        
        return this;
    }
    
    public StatefulKnowledgeSessionFluent update(FactHandle handle, Object object) {
        step.addCommand( new UpdateCommand( handle, object ) );
        return this;
    }
    
    public StatefulKnowledgeSessionFluent retract(FactHandle handle) {
        step.addCommand( new RetractCommand( handle ) );
        return this;
    }
    
    public StatefulKnowledgeSessionFluent fireAllRules() {
        step.addCommand( new FireAllRulesCommand() );
        return this;
    }
    
    public SimulationStepFluent end(String context, String name) {
        step.addCommand( new GetVariableCommand( StatefulKnowledgeSession.class.getName() ) );
        step.addCommand( new SetVariableCommandFromLastReturn( context, name ) );
        return step;
    }
    
    public SimulationStepFluent end(String name) {
        step.addCommand( new GetVariableCommand( StatefulKnowledgeSession.class.getName() ) );
        step.addCommand( new SetVariableCommandFromLastReturn( name ) );
        return step;
    }

    public SimulationStepFluent end() {
        return step;
    }

    public StatefulKnowledgeSessionFluent set(String name) {
        step.addCommand( new SetVariableCommandFromLastReturn( null, name ) );
        return this;
    }

    public StatefulKnowledgeSessionFluent startProcess(String identifier, Map<String, Object> params) {
        step.addCommand(new StartProcessCommand(identifier, params));
        return this;
    }

    public StatefulKnowledgeSessionFluent startProcess(String identifier) {
        step.addCommand(new StartProcessCommand(identifier));
        return this;
    }
    

    public StatefulKnowledgeSessionFluent createProcessInstance(String identifier, Map<String, Object> params) {
        step.addCommand(new CreateProcessInstanceCommand(identifier, params));
        return this;
    }

    public StatefulKnowledgeSessionFluent startProcessInstance(long processId) {
        step.addCommand(new StartProcessInstanceCommand(processId));
        return this;
    }

    public StatefulKnowledgeSessionFluent signalEvent(String id, Object event, long processId) {
        step.addCommand(new SignalEventCommand(processId, id, event));
        return this;
    }

    public StatefulKnowledgeSessionFluent signalEvent(String id, Object event) {
        step.addCommand(new SignalEventCommand(id, event));
        return this;
    }

}
