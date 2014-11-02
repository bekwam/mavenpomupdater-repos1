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

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Controller for Alert View
 * 
 * @author carlwalker
 * @since 1.1.0
 */
public class AlertController {

	private Log log = LogFactory.getLog( AlertController.class );
	
	@FXML
	GridPane gp;
	
	@FXML
	Label messageLabel;
	
	@FXML
	Label detailsLabel;
	
	@FXML
	Button actionButton;
	
	@FXML
	Button cancelButton;
	
	@FXML
	Button okButton;
	
	@FXML
	public void initialize() {
		if( log.isDebugEnabled() ) {
			log.debug("[INIT]");
		}
	}

	@FXML
	public void dismiss() {
		
		if( log.isDebugEnabled() ) {
			log.debug("[DISMISS]");
		}
		
		Parent flowPane = gp.getParent();
		flowPane.toBack();
	}
	
	public void setNotificationDialog(String message, String details) {
		cancelButton.setVisible(false);
		okButton.setVisible(false);
		actionButton.setText( "Dismiss" );
		messageLabel.setText( message );
		detailsLabel.setText( details );
	}
}
