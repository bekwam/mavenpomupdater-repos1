/*
 * Copyright 2014 Bekwam, Inc 
 * 
 * Licensed under the Apache License, Version 2.0 (the “License”); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at: 
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an “AS IS” BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 */
package com.bekwam.mavenpomupdater;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Model object for a POM
 * 
 * @author carlwalker
 * @since 1.1.0
 */
public class POMObject {

    private final SimpleStringProperty absPath = new SimpleStringProperty();
    private final SimpleStringProperty version = new SimpleStringProperty();
    private final SimpleStringProperty parentVersion = new SimpleStringProperty();
    private final SimpleBooleanProperty parseError = new SimpleBooleanProperty();
    
    public POMObject() {}

    public POMObject(String absPath, String version, String parentVersion, Boolean parseError) {
        this.absPath.set( absPath );
        this.version.set( version );
        this.parentVersion.set(parentVersion);
        this.parseError.set(parseError);
    }

    public String getAbsPath() { return absPath.get(); }
    public void setAbsPath(String _absPath) {
        absPath.set( _absPath );
    }

    public String getVersion() { return version.get(); }
    public void setVersion(String _version) { version.set(_version); }

    public String getParentVersion() { return parentVersion.get(); }
    public void setParentVersion(String _parentVersion) {
        parentVersion.set(_parentVersion);
    }

	public Boolean getParseError() {
		return parseError.get();
	}
	public void setParseError(Boolean _parseError) {
		parseError.set(_parseError);
	}

	@Override
	public String toString() {
		return "POMObject [absPath=" + absPath + ", version=" + version
				+ ", parentVersion=" + parentVersion + ", parseError="
				+ parseError + "]";
	}
	
	
}
