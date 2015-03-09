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
		
		aboutTab.setOnCloseRequest(evt -> {
				if( log.isDebugEnabled() ) {
					log.debug("[ABOUT CLOSED]");
				}
				
				if( tabPane.getTabs().contains( aboutTab ) ) {
					tabPane.getTabs().remove( aboutTab );
				}
			
				// needs alt selection here too?
				
				evt.consume();
		});
		
		aboutVersionLabel.setText( version );
		
		tabPane.getTabs().remove( aboutTab );  // start w/o it showing

	}
	
}
