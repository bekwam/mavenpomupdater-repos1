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
package com.bekwam.mavenpomupdater;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ErrorLogDelegate {

	private Log log = LogFactory.getLog(ErrorLogDelegate.class);
	
	TabPane tabPane;
	Tab errorLogTab;
	CheckMenuItem miErrorLog;
	TableView<ErrorLogEntry> tblErrors;
	
	public void init() {
	
		if( log.isDebugEnabled() ) {
			log.debug("[INIT]");
		}
		
		tabPane.getTabs().remove( errorLogTab );  // start w/o it showing
	}
	
	public void log(String fileName, String message) {
		
		if( log.isDebugEnabled() ) {
			log.debug("[LOG] fileName=" + fileName + ", message=" + message);
		}
		
		LocalDateTime now = LocalDateTime.now();
		String logTime = now.format( DateTimeFormatter.ISO_LOCAL_DATE_TIME );
		
		tblErrors.getItems().add( new ErrorLogEntry(logTime, fileName, message) );
	}

	public void closingTab() {
		if( log.isDebugEnabled() ) {
			log.debug("[CLOSING]");
		}
		miErrorLog.setSelected(false);
	}
	
	public void clearTable() {
		if( log.isDebugEnabled() ) {
			log.debug("[CLEAR]");
		}
		tblErrors.getItems().clear();
	}
}
