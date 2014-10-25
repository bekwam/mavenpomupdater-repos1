package com.bekwam.mavenpomupdater;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles initialization of About Screen
 * 
 * @author carlwalker
 * @since 1.1.0
 */
public class AboutDelegate {

	private Log log = LogFactory.getLog( AboutDelegate.class );
	
	TabPane tabPane;
	Tab aboutTab;
	Label aboutVersionLabel;
	ImageView imageView;
	String version;
	
	public void init() {
		
		if( log.isDebugEnabled() ) {
			log.debug("[INIT] version=" + version);
		}
		
		Image image = new Image("images/mpu_about.jpg");
		imageView.setImage( image );
		
		aboutTab.setOnCloseRequest(new EventHandler<Event>() {
			@Override
			public void handle(Event arg0) {
				if( log.isDebugEnabled() ) {
					log.debug("[ABOUT CLOSED]");
				}
				
				if( tabPane.getTabs().contains( aboutTab ) ) {
					tabPane.getTabs().remove( aboutTab );
				}
			
				// needs alt selection here too?
				
				arg0.consume();
			}	
		});
		
		aboutVersionLabel.setText( version );
		
		tabPane.getTabs().remove( aboutTab );  // start w/o it showing

	}
	
}
