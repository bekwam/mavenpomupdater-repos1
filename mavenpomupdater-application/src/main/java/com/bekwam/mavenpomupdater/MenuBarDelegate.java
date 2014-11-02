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

import java.net.URI;

import javafx.application.Platform;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

import org.apache.commons.lang3.StringUtils;
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
	MenuItem miCut, miCopy, miPaste;
	Clipboard systemClipboard;
	TextField tfRootDir, tfFilters, tfNewVersion;

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

	public void cut() {
		if( log.isDebugEnabled() ) {
			log.debug("[CUT]");
		}
		
		TextField focusedTF = getFocusedTextField();

		String text = focusedTF.getSelectedText();
		
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		systemClipboard.setContent(content);
		
		IndexRange range = focusedTF.getSelection();
		String origText = focusedTF.getText();
		String firstPart = StringUtils.substring( origText, 0, range.getStart() );
		String lastPart = StringUtils.substring( origText, range.getEnd(), StringUtils.length(origText) );
		focusedTF.setText( firstPart + lastPart );
		
		focusedTF.positionCaret( range.getStart() );

	}
	
	public void copy() {
		if( log.isDebugEnabled() ) {
			log.debug("[COPY]");
		}
		String text = getSelectedText();
		
		if( log.isDebugEnabled() ) {
			log.debug("[COPY] copied=" + text);
		}
		
		ClipboardContent content = new ClipboardContent();
		content.putString(text);
		systemClipboard.setContent(content);
	}

	public void paste() {
		if( log.isDebugEnabled() ) {
			log.debug("[PASTE]");
		}
		
		if( !systemClipboard.hasContent(DataFormat.PLAIN_TEXT) ) {
			adjustForEmptyClipboard();
			return;
		}
		
		String clipboardText = systemClipboard.getString();
		
		if( log.isDebugEnabled() ) {
			log.debug("[PASTE] pasting clipboard text=" + clipboardText);
		}
		
		TextField focusedTF = getFocusedTextField();
		IndexRange range = focusedTF.getSelection();
		
		if( log.isDebugEnabled() ) {
			log.debug("[PASTE] range start=" + range.getStart() + ", end=" + range.getEnd());
		}
		
		String origText = focusedTF.getText();
		
		int endPos = 0;
		String updatedText = "";
		String firstPart = StringUtils.substring( origText, 0, range.getStart() );
		String lastPart = StringUtils.substring( origText, range.getEnd(), StringUtils.length(origText) );
		if( log.isDebugEnabled() ) {
			log.debug("[PASTE] first=" + firstPart + ", last=" + lastPart);
		}
		updatedText = firstPart + clipboardText + lastPart;
		
		if( range.getStart() == range.getEnd() ) {
			endPos = range.getEnd() + StringUtils.length(clipboardText);
		} else {
			endPos = range.getStart() + StringUtils.length(clipboardText);
		}
		
		focusedTF.setText( updatedText );
		focusedTF.positionCaret( endPos );
	}

	private void adjustForEmptyClipboard() {
		if( log.isDebugEnabled() ) {
			log.debug("[ADJUST EMPTY CLIPBOARD]");
		}
		miPaste.setDisable(true);  // nothing to paste
	}

	private void adjustForClipboardContents() {
		if( log.isDebugEnabled() ) {
			log.debug("[ADJUST CLIPBOARD CONTENTS]");
		}
		miPaste.setDisable(false);  // something to paste	
	}
	
	private void adjustForSelection() {
		if( log.isDebugEnabled() ) {
			log.debug("[ADJUST FOR SELECTION]");
		}
		miCut.setDisable(false);
		miCopy.setDisable(false);
	}

	private void adjustForDeselection() {
		if( log.isDebugEnabled() ) {
			log.debug("[ADJUST FOR DE-SELECTION]");
		}
		miCut.setDisable(true);
		miCopy.setDisable(true);
	}

	public void showingEditMenu() {
		
		if( log.isDebugEnabled() ) {
			log.debug("[SHOWING EDIT MENU]");
		}
		
		//
		// Controls the enabling and disabling of edit menu items by consulting
		// the clipboard prior to showing the menu
		//
		
		if( systemClipboard == null ) {
			systemClipboard = Clipboard.getSystemClipboard();
		}
		
		if( systemClipboard.hasString() ) {
			if( log.isDebugEnabled() ) {
				log.debug("[SHOWING EDIT MENU] there is content in clipboard");
			}
			adjustForClipboardContents();
		} else {
			if( log.isDebugEnabled() ) {
				log.debug("[SHOWING EDIT MENU] there is NO content in clipboard");
			}
			adjustForEmptyClipboard();
		}
		
		if( anythingSelected() ) {
			if( log.isDebugEnabled() ) {
				log.debug("[SHOWING EDIT MENU] there is a selection");
			}
			adjustForSelection();

		} else {
			if( log.isDebugEnabled() ) {
				log.debug("[SHOWING EDIT MENU] there is a DE-selection");
			}
			adjustForDeselection();
		}
	}
	
	private boolean anythingSelected() {
		TextField[] tfs = new TextField[] { tfNewVersion, tfFilters, tfRootDir };
		for( TextField tf : tfs ) {
			if( StringUtils.isNotEmpty(tf.getSelectedText() ) ) {
				return true;
			}
		}
		return false;
	}
	
	private String getSelectedText() {
		TextField[] tfs = new TextField[] { tfNewVersion, tfFilters, tfRootDir };
		for( TextField tf : tfs ) {
			if( StringUtils.isNotEmpty(tf.getSelectedText() ) ) {
				return tf.getSelectedText();
			}
		}
		return null;
	}
	
	private TextField getFocusedTextField() {
		TextField[] tfs = new TextField[] { tfNewVersion, tfFilters, tfRootDir };
		for( TextField tf : tfs ) {
			if( tf.isFocused() ) {
				return tf;
			}
		}
		return null;		
	}
}
