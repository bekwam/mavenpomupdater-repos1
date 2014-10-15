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
