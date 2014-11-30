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

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

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
	
	Consumer<MainViewController> okCallback;
	
	WeakReference<MainViewController> mainViewControllerRef;
	
	@FXML
	public void initialize() {
		if( log.isDebugEnabled() ) {
			log.debug("[INIT]");
		}
		
        gp.getStyleClass().add("alert-pane");
	}

	@FXML
	public void action() {
		
		if( log.isDebugEnabled() ) {
			log.debug("[ACTION]");
		}
		
		Parent flowPane = gp.getParent();
		flowPane.toBack();
	}

	@FXML
	public void ok() {
		
		if( log.isDebugEnabled() ) {
			log.debug("[OK]");
		}
		
		okCallback.accept(mainViewControllerRef.get());
		
		Parent flowPane = gp.getParent();
		flowPane.toBack();
	}

	@FXML
	public void cancel() {
		
		if( log.isDebugEnabled() ) {
			log.debug("[CANCEL]");
		}
		
		Parent flowPane = gp.getParent();
		flowPane.toBack();
	}

	public void setNotificationDialog(String message, String details) {
		actionButton.setVisible(true);
		cancelButton.setVisible(false);
		okButton.setVisible(false);
		actionButton.setText( "Dismiss" );
		messageLabel.setText( message );
		detailsLabel.setText( details );
	}
	
	public void setConfirmationDialog(String message, 
									  String details, 
									  Consumer<MainViewController> okCallback) {
		
		actionButton.setVisible(false);
		cancelButton.setVisible(true);
		okButton.setVisible(true);
		messageLabel.setText( message );
		detailsLabel.setText( details );
		this.okCallback = okCallback;
	}
}
