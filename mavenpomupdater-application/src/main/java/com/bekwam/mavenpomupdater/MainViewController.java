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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

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
    TextField tfRootDir;

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

    AlertController alertController;
    
    public MainViewController() {
    	
    	if( log.isDebugEnabled() ) {
    		log.debug("[CONTROLLER]");
    	}
    }

    @FXML
    public void initialize() {
    	
    	if( log.isDebugEnabled() ) {
    		log.debug("[INIT]");
    	}

        tcPath.setCellValueFactory(
                new PropertyValueFactory<POMObject, String>("absPath")
        );

        tcVersion.setCellValueFactory(
                new PropertyValueFactory<POMObject, String>("version")
        );

        tcParentVersion.setCellValueFactory(
               new PropertyValueFactory<POMObject, String>("parentVersion")
        );
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
            System.out.println("dir=" + dir.getAbsolutePath());
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
        	return;
        }
        
        String filtersCSV = tfFilters.getText();

        CSVFilenameFilter ff = new CSVFilenameFilter(filtersCSV);

        List<String> pomPaths = new ArrayList<String>();
        gatherPOMPaths( rootDir, pomPaths, ff );

        if( CollectionUtils.isEmpty(pomPaths) ) {
        	if( log.isDebugEnabled() ) {
        		log.debug("[SCAN] pomPaths is empty");
        	}
        	alertController.setNotificationDialog("No POMs Found", "No pom.xml files were found in the specified Project Root.");
        	vbox.toBack();  // bring up the alert view
        	return;
        }

        tblPOMS.getItems().clear();
        for( String path : pomPaths ) {
            POMObject pomObject = parseFile( path );
            tblPOMS.getItems().add( pomObject );
        }
    }

    @SuppressWarnings("unused")
	private POMObject parseFile(String path) {
    	
    	if( log.isDebugEnabled() ) {
    		log.debug("[PARSE] path=" + path);
    	}

    	try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
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

            XPath pvXPath = XPathFactory.newInstance().newXPath();
            XPathExpression pvExpression = xpath.compile("//project/parent/version/text()");
            Node pvNode = (Node) pvExpression.evaluate( doc, XPathConstants.NODE );

            String pVersion = "";
            if( pvNode != null ) {
                pVersion = pvNode.getNodeValue();
                if( log.isDebugEnabled() ) {
                	log.debug("[PARSE]    parentVersion=" + pvNode.getNodeValue());
                }
            }

            return new POMObject(path, version, pVersion);

        } catch(Exception exc) {
            exc.printStackTrace();
            return new POMObject(path, "", "");
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
        if( StringUtils.isEmpty(newVersion) ) {
        	if( log.isDebugEnabled() ) {
        		log.debug("[UPDATE] newVersion is empty");
        	}
        	alertController.setNotificationDialog("No Version Specified", "Please specify a version.");
        	vbox.toBack();  // bring up the alert view
        	return;
        }

        if( CollectionUtils.isEmpty(tblPOMS.getItems()) ) {
        	if( log.isDebugEnabled() ) {
        		log.debug("[UPDATE] tblPOMS is empty");
        	}
        	alertController.setNotificationDialog("No POMs Specified", "No poms were specified.\nBrowser for a Project Root and press Scan.");
        	vbox.toBack();  // bring up the alert view
        	return;
        }

        for( POMObject p : tblPOMS.getItems() ) {

        	if( log.isDebugEnabled() ) {
        		log.debug("[UPDATE] p=" + p.getAbsPath());
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
                    node.setNodeValue( tfNewVersion.getText() );
                }

                if( p.getVersion() != null && p.getVersion().length() > 0 ) {
                    XPath xpath = XPathFactory.newInstance().newXPath();
                    XPathExpression expression = xpath.compile("//project/version/text()");
                    Node node = (Node) expression.evaluate(doc, XPathConstants.NODE);
                    node.setNodeValue( tfNewVersion.getText() );
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
    }
}
