package com.bekwam.mavenpomupdater;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by carl_000 on 9/17/2014.
 */
public class POMObject {

    private final SimpleStringProperty absPath = new SimpleStringProperty();
    private final SimpleStringProperty version = new SimpleStringProperty();
    private final SimpleStringProperty parentVersion = new SimpleStringProperty();

    public POMObject() {}

    public POMObject(String absPath, String version, String parentVersion) {
        this.absPath.set( absPath );
        this.version.set( version );
        this.parentVersion.set(parentVersion);
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
}
