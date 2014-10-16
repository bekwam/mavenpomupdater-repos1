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
