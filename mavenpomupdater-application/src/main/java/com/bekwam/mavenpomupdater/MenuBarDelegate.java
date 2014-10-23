package com.bekwam.mavenpomupdater;

import java.net.URI;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles actions initiated by the MenuBar
 * 
 * @author carlwalker
 * @since 1.1.0
 */
public class MenuBarDelegate {

	private Log log = LogFactory.getLog(MenuBarDelegate.class);
	
	TabPane tabPane;
	Tab homeTab;
	Tab aboutTab;
	String supportURL;
	String licenseURL;
	
	public void close() {
		
		if( log.isDebugEnabled() ) {
			log.debug("[CLOSE]");
		}
		
		Platform.exit();
	}
	
	public void showAbout() {
		
		if( log.isDebugEnabled() ) {
			log.debug("[SHOW ABOUT]");
		}
		
		if( !tabPane.getTabs().contains( aboutTab ) ) {
			tabPane.getTabs().add( aboutTab );
		}
		
		tabPane.getSelectionModel().select(aboutTab);
	}
	
	public void browseSupport() {
		if( log.isDebugEnabled() ) {
			log.debug("[BROWSE SUPPORT]");
		}
		
		try {
			java.awt.Desktop.getDesktop().browse(new URI(supportURL));
		} catch(Exception exc) {
			log.error("cannot browse support to " + supportURL, exc);
		}
	}
	
	public void browseLicense() {
		if( log.isDebugEnabled() ) {
			log.debug("[BROWSE LICENSE]");
		}
		
		try {
			java.awt.Desktop.getDesktop().browse(new URI(licenseURL));
		} catch(Exception exc) {
			log.error("cannot browse license to " + licenseURL, exc);
		}
	}

}
