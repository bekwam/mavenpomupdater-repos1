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

import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handles actions initiated by the ToolBar
 * 
 * Since the ToolBar is always showing, this requires a different strategy than
 * what was used for the MenuBar.  The TextFields will invoke an update() method
 * on this class to notify the ToolBar that there is an active selection or
 * deselection.
 * 
 * The application will also need to notify the ToolBar of updates in case the
 * clipboard has been modified outside of the app.
 * 
 * This class has to keep track of the selection because it's lost on clicking
 * the ToolBar Buttons.
 * 
 * @author carlwalker
 * @since 1.1.0
 */
public class ToolBarDelegate {

	private Log log = LogFactory.getLog(ToolBarDelegate.class);
	
	Button tbCut, tbCopy, tbPaste;
	Clipboard systemClipboard;
	TextField tfRootDir, tfFilters, tfNewVersion;
	TextField lastFocusedTF;
	String lastSelectedText;
	IndexRange lastSelectedRange;
	
	public void init() {
		if( log.isDebugEnabled() ) {
			log.debug("[INIT]");
		}
		
		if( systemClipboard == null ) {
			systemClipboard = Clipboard.getSystemClipboard();
		}

		if( systemClipboard.hasContent(DataFormat.PLAIN_TEXT) ) {
			tbPaste.setDisable(false);
		}

	}
	
	public void cut() {
		if( log.isDebugEnabled() ) {
			log.debug("[CUT]");
		}
		
		ClipboardContent content = new ClipboardContent();
		content.putString(lastSelectedText);
		systemClipboard.setContent(content);
		
		lastFocusedTF.deleteText( lastSelectedRange );
		lastFocusedTF.positionCaret( lastSelectedRange.getStart() );
	}
	
	public void copy() {
		if( log.isDebugEnabled() ) {
			log.debug("[COPY] lftf=" + lastFocusedTF.getId());
		}
		
		if( log.isDebugEnabled() ) {
			log.debug("[COPY] copied=" + lastSelectedText);
		}
		
		ClipboardContent content = new ClipboardContent();
		content.putString(lastSelectedText);
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
		
		if( log.isDebugEnabled() ) {
			log.debug("[PASTE] range start=" + lastSelectedRange.getStart() + ", end=" + lastSelectedRange.getEnd());
		}
		
		int endPos = 0;
		if( lastSelectedRange.getStart() == lastSelectedRange.getEnd() ) {
			endPos = lastSelectedRange.getEnd() + StringUtils.length(clipboardText);
		} else {
			endPos = lastSelectedRange.getStart() + StringUtils.length(clipboardText);
		}
		
		lastFocusedTF.replaceText( lastSelectedRange, clipboardText );
		lastFocusedTF.requestFocus();
		lastFocusedTF.positionCaret( endPos );
	}

	private void adjustForEmptyClipboard() {
		if( log.isDebugEnabled() ) {
			log.debug("[ADJUST EMPTY CLIPBOARD]");
		}
		tbPaste.setDisable(true);  // nothing to paste
	}

	private void adjustForClipboardContents() {
		if( log.isDebugEnabled() ) {
			log.debug("[ADJUST CLIPBOARD CONTENTS]");
		}
		tbPaste.setDisable(false);  // something to paste	
	}
	
	private void adjustForSelection() {
		if( log.isDebugEnabled() ) {
			log.debug("[ADJUST FOR SELECTION]");
		}
		tbCut.setDisable(false);
		tbCopy.setDisable(false);
	}

	private void adjustForDeselection() {
		if( log.isDebugEnabled() ) {
			log.debug("[ADJUST FOR DE-SELECTION]");
		}
		tbCut.setDisable(true);
		tbCopy.setDisable(true);
	}

	public void updateToolBarForClipboard(TextField focusedTF, String selectedText, IndexRange selectedRange) {
		
		if( log.isDebugEnabled() ) {
			log.debug("[UPDATE TBB]");
		}
		
		if( systemClipboard == null ) {
			systemClipboard = Clipboard.getSystemClipboard();
		}
		
		if( systemClipboard.hasString() ) {
			if( log.isDebugEnabled() ) {
				log.debug("[UPDATE TBB] there is content in clipboard");
			}
			adjustForClipboardContents();
		} else {
			if( log.isDebugEnabled() ) {
				log.debug("[UPDATE TBB] there is NO content in clipboard");
			}
			adjustForEmptyClipboard();
		}
		
		if( StringUtils.isNotEmpty(focusedTF.getSelectedText()) ) {
			if( log.isDebugEnabled() ) {
				log.debug("[UPDATE TBB] there is a selection");
			}
			adjustForSelection();

		} else {
			if( log.isDebugEnabled() ) {
				log.debug("[UPDATE TBB] there is a de-selection");
			}
			adjustForDeselection();
		}
		
		if( log.isDebugEnabled() ) {
			log.debug("[UPDATE TBB]  assigining tf=" + focusedTF.getId() + " as last focused; text=" + selectedText);
		}
		
		lastFocusedTF = focusedTF;
		lastSelectedText = selectedText;
		lastSelectedRange = selectedRange;
	}
	
}
