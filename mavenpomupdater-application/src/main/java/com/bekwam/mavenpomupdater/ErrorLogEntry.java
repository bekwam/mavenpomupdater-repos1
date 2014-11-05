package com.bekwam.mavenpomupdater;

import javafx.beans.property.SimpleStringProperty;

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
 * 
 * @author carlwalker
 * @since 1.2.0
 */
public class ErrorLogEntry {

	private final SimpleStringProperty logTime = new SimpleStringProperty();
	private final SimpleStringProperty fileName = new SimpleStringProperty();
	private final SimpleStringProperty message = new SimpleStringProperty();
	
	public ErrorLogEntry() {}
	
	public ErrorLogEntry(String logTime, String fileName, String message) {
		this.logTime.set( logTime );
		this.fileName.set( fileName );
		this.message.set( message );
	}
	
    public String getLogTime() { return logTime.get(); }
    public void setLogTime(String _logTime) {
        logTime.set( _logTime );
    }
    
    public String getFileName() { return fileName.get(); }
    public void setFileName(String _fileName) {
    	fileName.set(_fileName);
    }
    
    public String getMessage() { return message.get(); }
    public void setMessage(String _message) {
    	message.set(_message);
    }

	@Override
	public String toString() {
		return "ErrorLogEntry [logTime=" + logTime + ", fileName=" + fileName
				+ ", message=" + message + "]";
	}

    
}
