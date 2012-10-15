/**
 * Copyright 2010 JBoss Inc
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

package org.drools.persistence.variablepersistence;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author JBoss
 */

@Entity
public class StringPersistedVariable implements Serializable {
	private static final long serialVersionUID = -7114851874329560372L;

	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private Long id;
	private Long processInstanceId;
	private String name;
	private String placeholder;
	private String string;

    public StringPersistedVariable() {
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
    
    public Long getProcessInstanceId() {
		return processInstanceId;
	}
    
    public Long getId() {
		return id;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
    
    public void setId(Long id) {
		this.id = id;
	}
    
    public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
    
    public String toString() {
    	return getClass().getName() + " id=" + id + " name=" + name + " placeholder=" + placeholder + " string=" + string;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StringPersistedVariable other = (StringPersistedVariable) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.placeholder == null) ? (other.placeholder != null) : !this.placeholder.equals(other.placeholder)) {
            return false;
        }
        if ((this.string == null) ? (other.string != null) : ! this.string.equals(other.string)) {
        	return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 41 * hash + (this.placeholder != null ? this.placeholder.hashCode() : 0);
        hash = 41 * hash + (this.string != null ? this.string.hashCode() : 0);
        return hash;
    }
}
