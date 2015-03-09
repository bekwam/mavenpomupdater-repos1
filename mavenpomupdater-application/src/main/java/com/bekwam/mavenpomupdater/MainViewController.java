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

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.bekwam.mavenpomupdater.data.PreferencesConstants;
import com.bekwam.mavenpomupdater.data.PreferencesDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.bekwam.mavenpomupdater.data.FavoritesDAO;

/**
 * Controller to support the main screen of MPU
 * 
 * @author carlwalker
 * @since 1.0.0
 */
public class MainViewController {

	private Log log = LogFactory.getLog( MainViewController.class);
	
	@FXML
	VBox vbox;
	
	@FXML
	GridPane gp; // for hi-dpi treatment
	
    @FXML
    ComboBox<String> cbRootDir;

    @FXML
    TextField tfFilters;

    @FXML
    TextField tfNewVersion;

    @FXML
    TableView<POMObject> tblPOMS;

    @FXML
    TableColumn<POMObject, String> tcPath;

    @FXML
    TableColumn<POMObject, String> tcVersion;

    @FXML
    TableColumn<POMObject, String> tcParentVersion;

    @FXML
    TableColumn<POMObject, Boolean> tcUpdate;

    @FXML
    ImageView aboutImageView;
    
    @FXML
    TabPane tabPane;
    
    @FXML
    Tab homeTab;
    
    @FXML
    Tab aboutTab;
    
    @FXML
    Tab errorLogTab;
    
    @FXML
    Label aboutVersionLabel;
    
    @FXML
    MenuItem miCut;
    
    @FXML
    MenuItem miCopy;
    
    @FXML
    MenuItem miPaste;
    
    @FXML
    CheckMenuItem miErrorLog;
    
    @FXML
    Button tbCut;
    
    @FXML
    Button tbCopy;
    
    @FXML
    Button tbPaste;
    
    @FXML
    Button tbScan;
    
    @FXML
    Button tbUpdate;
    
    @FXML
    Button tbClear;
    
    @FXML
    TableView<ErrorLogEntry> tblErrors;
    
    @FXML
    TableColumn<ErrorLogEntry, String> tcTime;
    
    @FXML
    TableColumn<ErrorLogEntry, String> tcFile;
    
    @FXML
    TableColumn<ErrorLogEntry, String> tcMessage;

    @FXML
    AnchorPane ap;

    @FXML
    Button btnSelectAll;

    @FXML
    Button btnDeselectAll;

    @FXML
    Button btnLockUnlock;

    @FXML
    Tab preferencesTab;

    @Inject
    MenuBarDelegate menuBarDelegate;
    
    @Inject
    AboutDelegate aboutDelegate;
    
    @Inject
    ToolBarDelegate toolBarDelegate;
    
    @Inject
    ErrorLogDelegate errorLogDelegate;

    @Inject
    PreferencesDelegate preferencesDelegate;

    //
    // will be manually wired
    //
    AlertController alertController;   
    
    @Inject
    FavoritesDAO favoritesDAO;
    
    @Inject
    PropertiesFileDAO propertiesFileDAO;

    @Inject
    PreferencesDAO preferencesDAO;

    private DocumentBuilderFactory factory;    
    private TextField tfRootDir;
    private Boolean tblPOMSLocked = true;
    private Boolean tblPOMSDirty = false;
    private ImageView lockImageView;
    private ImageView unlockImageView;

    public MainViewController() {
    	
    	if( log.isDebugEnabled() ) {
    		log.debug("[CONTROLLER]");
    	}
    	
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);        
    }

    @FXML
    public void initialize() {
    	
    	if( log.isDebugEnabled() ) {
    		log.debug("[INIT]");
    	}

    	vbox.getStyleClass().add("main-view-pane");

        initializeTblPOMS();
        initializeButtonGraphics();
        initializeDelegates();

        errorLogTab.setOnSelectionChanged(event -> tbClear.setDisable( !errorLogTab.isSelected() ) );

        List<String> favorites = favoritesDAO.findAllFavoriteRootDirs();
        
        cbRootDir.getItems().addAll( favorites );        
    }

    private void initializeDelegates() {
        Properties appProperties = propertiesFileDAO.getProperties();
        String version = appProperties.getProperty(AppPropertiesKeys.VERSION);

        //
        // tfRootDir comes from a ComboBox that isn't readily accessible
        //

        tfRootDir = cbRootDir.editorProperty().get();
        tfRootDir.setOnMouseReleased( event -> { toolBarMouseReleased(event); });

        //
        // wire up delegates
        //
        aboutDelegate.imageView = aboutImageView;
        aboutDelegate.tabPane = tabPane;
        aboutDelegate.aboutTab = aboutTab;
        aboutDelegate.version = version;
        aboutDelegate.aboutVersionLabel = aboutVersionLabel;

        menuBarDelegate.tabPane = tabPane;
        menuBarDelegate.homeTab = homeTab;
        menuBarDelegate.aboutTab = aboutTab;
        menuBarDelegate.errorLogTab = errorLogTab;
        menuBarDelegate.preferencesTab = preferencesTab;
        menuBarDelegate.supportURL = appProperties.getProperty(AppPropertiesKeys.SUPPORT_URL);
        menuBarDelegate.licenseURL = appProperties.getProperty(AppPropertiesKeys.LICENSE_URL);
        menuBarDelegate.miCut = miCut;
        menuBarDelegate.miCopy = miCopy;
        menuBarDelegate.miPaste = miPaste;
        menuBarDelegate.tfFilters = tfFilters;
        menuBarDelegate.tfNewVersion = tfNewVersion;
        menuBarDelegate.tfRootDir = tfRootDir;
        menuBarDelegate.preferencesDelegate = preferencesDelegate;

        toolBarDelegate.tbCut = tbCut;
        toolBarDelegate.tbCopy = tbCopy;
        toolBarDelegate.tbPaste = tbPaste;
        toolBarDelegate.tfFilters = tfFilters;
        toolBarDelegate.tfNewVersion = tfNewVersion;
        toolBarDelegate.tfRootDir = tfRootDir;

        errorLogDelegate.tabPane = tabPane;
        errorLogDelegate.errorLogTab = errorLogTab;
        errorLogDelegate.tblErrors = tblErrors;
        errorLogDelegate.miErrorLog = miErrorLog;

        preferencesDelegate.tabPane = tabPane;
        preferencesDelegate.preferencesTab = preferencesTab;
        preferencesDelegate.tfFilters = tfFilters;

        //
        // initialize delegates
        //
        aboutDelegate.init();
        toolBarDelegate.init();
        errorLogDelegate.init();
        preferencesDelegate.init();

        favoritesDAO.init();
        preferencesDAO.init();
    }

    private void initializeTblPOMS() {
        tcPath.setCellValueFactory(
                new PropertyValueFactory<POMObject, String>("absPath")
        );
        tcPath.setCellFactory(new WarningCellFactory());

        tcVersion.setCellValueFactory(
                new PropertyValueFactory<POMObject, String>("version")
        );
        tcVersion.setCellFactory(new WarningCellFactory());
        tcVersion.setOnEditCommit(t -> {
            tblPOMSDirty = true;
            ((POMObject) t.getTableView().getItems().get(t.getTablePosition().getRow())).setVersion(t.getNewValue());
        });

        tcParentVersion.setCellValueFactory(
               new PropertyValueFactory<POMObject, String>("parentVersion")
        );
        tcParentVersion.setCellFactory(new WarningCellFactory());
        tcParentVersion.setOnEditCommit(t -> {
            tblPOMSDirty = true;
            ((POMObject) t.getTableView().getItems().get(t.getTablePosition().getRow())).setParentVersion(t.getNewValue());
        });

        tcUpdate.setCellValueFactory(
                new PropertyValueFactory<POMObject, Boolean>("update")
        );
        tcUpdate.setCellFactory(CheckBoxTableCell.forTableColumn(tcUpdate));

        tcTime.setCellValueFactory(
                new PropertyValueFactory<ErrorLogEntry, String>("logTime")
        );

        tcFile.setCellValueFactory(
                new PropertyValueFactory<ErrorLogEntry, String>("fileName")
        );

        tcMessage.setCellValueFactory(
                new PropertyValueFactory<ErrorLogEntry, String>("message")
        );
    }

    private void initializeButtonGraphics() {
        Image cutImage = new Image("images/cut32.png");
        tbCut.setGraphic(new ImageView(cutImage));

        Image copyImage = new Image("images/copy32.png");
        tbCopy.setGraphic(new ImageView(copyImage));

        Image pasteImage = new Image("images/paste32.png");
        tbPaste.setGraphic(new ImageView(pasteImage));

        Image scanImage = new Image("images/scan32.png");
        tbScan.setGraphic(new ImageView(scanImage));

        Image updateImage = new Image("images/update32.png");
        tbUpdate.setGraphic(new ImageView(updateImage));

        Image clearImage = new Image("images/clear32.png");
        tbClear.setGraphic( new ImageView(clearImage));

        Image selImage = new Image("images/select16.png");
        btnSelectAll.setGraphic( new ImageView(selImage) );

        Image deselImage = new Image("images/deselect16.png");
        btnDeselectAll.setGraphic( new ImageView(deselImage ) );

        Image lockImage = new Image("images/lock16.png");
        lockImageView = new ImageView(lockImage);

        Image unlockImage = new Image("images/unlock16.png");
        unlockImageView = new ImageView(unlockImage);
        btnLockUnlock.setGraphic(unlockImageView);
    }

    @FXML
    public void selectFile(ActionEvent evt) {
    	
    	if( log.isDebugEnabled() ) {
    		log.debug("[SELECT FILE]");
    	}

        Button b = (Button)evt.getSource();

        Window w = b.getParent().getScene().getWindow();

        DirectoryChooser dirChooser = new DirectoryChooser();

        File dir = dirChooser.showDialog(w);
        if (dir != null) {
            tfRootDir.setText( dir.getAbsolutePath() );
        }
    }

    @FXML
    public void scan() {
    	
    	if( log.isDebugEnabled() ) {
    		log.debug("[SCAN]");
    	}

        String rootDir = tfRootDir.getText();

        if( StringUtils.isEmpty(rootDir) ) {

        	if( log.isDebugEnabled() ) {
        		log.debug("[SCAN] rootDir is empty");
        	}

            alertController.setNotificationDialog("Project Root Is Empty", "A root directory must be specified in order to scan.");
        	vbox.toBack();  // bring up the alert view

            tblPOMS.getItems().clear();
            btnDeselectAll.setDisable( true );
            btnSelectAll.setDisable( true );

            lockTblPOMS();
            btnLockUnlock.setDisable( true );

            tfNewVersion.setDisable( false );

            return;
        }
        
        //
        // record the rootDir as a favorite (if needed)
        //
        favoritesDAO.addFavoriteRootDir(rootDir);
        
        if( !cbRootDir.getItems().contains(rootDir) ) {
        	cbRootDir.getItems().add(rootDir);
        }
        
        String filtersCSV = preferencesDelegate.getProperty(PreferencesConstants.KEY_FILTERS);

        CSVFilenameFilter ff = new CSVFilenameFilter(filtersCSV);

        List<String> pomPaths = new ArrayList<String>();
        gatherPOMPaths( rootDir, pomPaths, ff );

        if( CollectionUtils.isEmpty(pomPaths) ) {

            if( log.isDebugEnabled() ) {
        		log.debug("[SCAN] pomPaths is empty");
        	}

            alertController.setNotificationDialog("No POMs Found", "No pom.xml files were found in the specified Project Root.");
        	vbox.toBack();  // bring up the alert view

            tblPOMS.getItems().clear();
            btnDeselectAll.setDisable( true );
            btnSelectAll.setDisable( true );

            lockTblPOMS();
            btnLockUnlock.setDisable(true);

            tfNewVersion.setDisable( false );

            return;
        }

        tblPOMS.getItems().clear();
        for( String path : pomPaths ) {
            POMObject pomObject = parseFile( path );
            tblPOMS.getItems().add( pomObject );
        }

        //
        // Activate the select and de-select buttons
        //
        btnDeselectAll.setDisable( false );
        btnSelectAll.setDisable(false);

        //
        // Activate the lock/unlock button
        //
        lockTblPOMS();  // reset lock state
        btnLockUnlock.setDisable(false);
        tblPOMSDirty = false;
        tfNewVersion.setDisable( false );
    }

	private POMObject parseFile(String path) {
    	
    	if( log.isDebugEnabled() ) {
    		log.debug("[PARSE] path=" + path);
    	}

    	try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expression = xpath.compile("//project/version/text()");
            Node node = (Node) expression.evaluate( doc, XPathConstants.NODE);

            String version = "";
            if( node != null ) {
                version = node.getNodeValue();
                if( log.isDebugEnabled() ) {
                	log.debug("[PARSE]    version=" + node.getNodeValue());
                }
            }

 //           XPath pvXPath = XPathFactory.newInstance().newXPath();
            XPathExpression pvExpression = xpath.compile("//project/parent/version/text()");
            Node pvNode = (Node) pvExpression.evaluate( doc, XPathConstants.NODE );

            String pVersion = "";
            if( pvNode != null ) {
                pVersion = pvNode.getNodeValue();
                if( log.isDebugEnabled() ) {
                	log.debug("[PARSE]    parentVersion=" + pvNode.getNodeValue());
                }
            }

            return new POMObject(true, path, version, pVersion, false);

        } catch(Exception exc) {
        	log.error( "error parsing path=" + path, exc );
        	
        	errorLogDelegate.log( path, exc.getMessage() );
        	
            return new POMObject(false, path, "Parse Error (will be skipped)", "Parse Error (will be skipped)", true);
        }
    }

    private void gatherPOMPaths(String rootDir, List<String> pomPaths, FilenameFilter ff) {

        if( StringUtils.isEmpty(rootDir) ) {
        	
        	if( log.isDebugEnabled() ) {
        		log.debug("[GATHER] rootDir is empty");
        	}
        	
            return;
        }

        File rd = new File(rootDir);
        
        if( !rd.exists() ) {
        	if( log.isDebugEnabled() ) {
        		log.debug("[SCAN] rootDir does not exist");
        	}
        	alertController.setNotificationDialog("Project Root Does Not Exist", "The specified root directory '" + rootDir + "' does not exist.");
        	vbox.toBack();  // bring up the alert view
        	return;
        }

        if( rd.isFile() ) {

            if( rd.getName().equals("pom.xml") ) {
                pomPaths.add( rd.getAbsolutePath() );
            }

        } else {
            for (File f : rd.listFiles(ff)) {
                gatherPOMPaths(f.getAbsolutePath(), pomPaths, ff);
            }
        }
    }

    @FXML
    public void update() {
    	
    	if( log.isDebugEnabled() ) {
    		log.debug("[UPDATE]");
    	}

    	String newVersion = tfNewVersion.getText();
        if( StringUtils.isEmpty(newVersion) && !tblPOMSDirty ) {
        	if( log.isDebugEnabled() ) {
        		log.debug("[UPDATE] newVersion is empty and not manually editing the table");
        	}
        	alertController.setNotificationDialog("No Version Specified", "Please specify a version.");
        	vbox.toBack();  // bring up the alert view
        	return;
        }

        long npoms = tblPOMS.getItems().stream().filter(p -> p.getUpdate()).count();
        if( npoms == 0L ) {
        	if( log.isDebugEnabled() ) {
        		log.debug("[UPDATE] tblPOMS is empty");
        	}
        	alertController.setNotificationDialog("No POMs Specified", "No poms were specified.\nBrowse for a Project Root, press Scan, and select one or more poms.");
        	vbox.toBack();  // bring up the alert view
        	return;
        } else {
        	
        	if( log.isDebugEnabled() ) {
        		log.debug("[UPDATE] confirming update operation");
        	}
        	alertController.setConfirmationDialog(
        			"Confirm Update", 
        			"This wil update " + npoms + " POM files.  Continue?",
        			mv -> mv.doUpdate()
        			);
        
        	vbox.toBack();       	
        }
    }
    
    public void doUpdate() {

    	for( POMObject p : tblPOMS.getItems() ) {

    	    if( log.isDebugEnabled() ) {
    		    log.debug("[DO UPDATE] p=" + p.getAbsPath());
    	    }

    	    if( p.getParseError() ) {
    		    if( log.isDebugEnabled() ) {
    			    log.debug("[DO UPDATE] skipping update of p=" + p.getAbsPath() + " because of a parse error on scanning");
    		    }
    		    continue;
    	    }

            if( !p.getUpdate() ) {
                if( log.isDebugEnabled() ) {
                    log.debug("[DO UPDATE] skipping update of p=" + p.getAbsPath() + " because user excluded it from update");
                }
                continue;
            }

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(false);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(p.getAbsPath());

                if( p.getParentVersion() != null && p.getParentVersion().length() > 0 ) {
                    XPath xpath = XPathFactory.newInstance().newXPath();
                    XPathExpression expression = xpath.compile("//project/parent/version/text()");
                    Node node = (Node) expression.evaluate( doc, XPathConstants.NODE);

                    if( StringUtils.isNotEmpty(tfNewVersion.getText()) ) {
                        node.setNodeValue(tfNewVersion.getText());
                    } else { // editing individual table cells
                        node.setNodeValue( p.getParentVersion() );
                    }
                }

                if( p.getVersion() != null && p.getVersion().length() > 0 ) {
                    XPath xpath = XPathFactory.newInstance().newXPath();
                    XPathExpression expression = xpath.compile("//project/version/text()");
                    Node node = (Node) expression.evaluate(doc, XPathConstants.NODE);

                    if( StringUtils.isNotEmpty(tfNewVersion.getText()) ) {
                        node.setNodeValue(tfNewVersion.getText());
                    } else { // editing individual table cells
                        node.setNodeValue(p.getVersion());
                    }
                }

                TransformerFactory tFactory =
                    TransformerFactory.newInstance();
                Transformer transformer = tFactory.newTransformer();

                String workingFileName = p.getAbsPath() + ".mpu";
                FileWriter fw = new FileWriter(workingFileName);
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(fw);
                transformer.transform(source, result);
                fw.close();

                Path src = FileSystems.getDefault().getPath( workingFileName );
                Path target = FileSystems.getDefault().getPath( p.getAbsPath() );

                Files.copy(src, target, StandardCopyOption.REPLACE_EXISTING);

                Files.delete( src );

            } catch(Exception exc) {
                log.error( "error updating poms", exc );
            }
        }
    
        if( StringUtils.isNotEmpty(tfRootDir.getText()) ) {
            if( log.isDebugEnabled() ) {
    		    log.debug("[DO UPDATE] issuing rescan command");
            }
            scan();
        } else {
    	    if( log.isDebugEnabled() ) {
    		    log.debug("[DO UPDATE] did an update, but there is not value in root; clearing");
            }
            tblPOMS.getItems().clear();
        }
        tblPOMSDirty = false;
        tfNewVersion.setDisable( false );
    }

    @FXML
    public void close() {
    	menuBarDelegate.close();
    }
    
    @FXML
    public void showAbout() {
    	menuBarDelegate.showAbout();
    }

    @FXML
    public void showPreferences() { menuBarDelegate.showPreferences(); }

    @FXML
    public void showOrHideErrorLog(ActionEvent evt) {
    	CheckMenuItem mi = (CheckMenuItem)evt.getSource();
    	if( mi.isSelected() ) {
    		menuBarDelegate.showErrorLog();
    	} else {
    		menuBarDelegate.hideErrorLog();
    	}
    }

    @FXML
    public void browseSupport() {
    	menuBarDelegate.browseSupport();
    }
    
    @FXML
    public void browseLicense() {
    	menuBarDelegate.browseLicense();
    }
    
    @FXML
    public void cut() {
    	menuBarDelegate.cut();
    }
    
    @FXML
    public void copy() {
    	menuBarDelegate.copy();
    }

    @FXML
    public void paste() {
    	menuBarDelegate.paste();
    }

    @FXML
    public void tbCut() {
    	toolBarDelegate.cut();
    }
    
    @FXML
    public void tbCopy() {
    	toolBarDelegate.copy();
    }

    @FXML
    public void tbPaste() {
    	toolBarDelegate.paste();
    }

    @FXML
    public void showingEditMenu() {
    	menuBarDelegate.showingEditMenu();
    }

    @FXML
    public void toolBarMouseReleased(MouseEvent evt) {
    	
    	TextField tf = (TextField)evt.getSource();
    	String selectedText = tf.getSelectedText();
    	IndexRange selectedRange = tf.getSelection();
    	
    	toolBarDelegate.updateToolBarForClipboard(tf, selectedText, selectedRange);
    }

    public void adjustForHiDPI() {
    	if( log.isDebugEnabled() ) {
    		log.debug("[ADJUST]");
    	}
    	gp.setPrefHeight(270.0);
    }
    
    @FXML
    public void mouseTest(MouseEvent evt) {
    	log.debug("[MOUSE] evt type=" + evt.getEventType());
    	
    }
    
    @FXML
    public void closingErrorLogTab() {
    	errorLogDelegate.closingTab();
    }
    
    @FXML
    public void clearErrorLog() {
    	errorLogDelegate.clearTable();
    }

    @FXML
    public void savePreferences() { preferencesDelegate.savePreferences(); }

    @FXML
    public void revertPreferences() { preferencesDelegate.show(); }

    @FXML
    public void selectAllTblPOMS() {

        if( log.isDebugEnabled() ) {
            log.debug("[SELECT ALL]");
        }
        tblPOMS.getItems().
                stream().
                filter( pom -> !pom.getParseError()).
                forEach( pom -> pom.setUpdate(true) );
    }

    @FXML
    public void deSelectAllTblPOMS() {

        if( log.isDebugEnabled() ) {
            log.debug("[DESELECT ALL]");
        }

        tblPOMS.getItems().
                stream().
                forEach(pom -> pom.setUpdate(false ) );
    }

    @FXML
    public void handleLockTblPOMS() {

        if (log.isDebugEnabled() ) {
            log.debug("[HANDLE LOCK]");
        }

        if( tblPOMSLocked ) {  // unlock operation
            unlockTblPOMS();
        } else { // lock operation
            lockTblPOMS();
        }
    }

    private void unlockTblPOMS() {
        tblPOMSLocked = false;

        tcVersion.setEditable( true );
        tcParentVersion.setEditable( true );

        btnLockUnlock.setGraphic( lockImageView );

        //
        // operating in manual mode which ignores the global New Version
        //
        tfNewVersion.setText( null );
        tfNewVersion.setDisable( true );
    }

    private void lockTblPOMS() {
        tblPOMSLocked = true;

        tcVersion.setEditable( false );
        tcParentVersion.setEditable( false );

        btnLockUnlock.setGraphic( unlockImageView );

        tfNewVersion.setDisable( false );
    }
}

