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
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bekwam.mavenpomupdater.data.FavoritesDAO;

/**
 * Main entry point for MPU
 * 
 * Scene graph structure
 * 
 * Stack Pane --> FlowPane --> GridPane (from alert.fxml)
 *            +-> VBox (from manenpomupdater.fxml)
 * 
 * @author carlwalker
 * @since 1.1.0
 */
public class Main extends Application {

	private Log log = LogFactory.getLog(Main.class);
	
	PropertiesFileDAO versionFileDAO;
	FavoritesDAO favoritesDAO;
	
    @Override
    public void start(Stage primaryStage) throws Exception{
    	
    	//
    	// handle command line options
    	//
    	Application.Parameters params = getParameters();
    	
    	List<String> unnamedList = params.getUnnamed();

    	Options options = new Options();
		options.addOption( "help", false, "Print this message");
		options.addOption( "hidpi", false, "Use high-DPI scaling");
		
		CommandLineParser p = new BasicParser();
		CommandLine cmd = p.parse(options, unnamedList.toArray(new String[0]));
		
		HelpFormatter formatter = new HelpFormatter();
		
		if( cmd.hasOption("help") ) {
			if( log.isDebugEnabled() ) {
				log.debug("[START] running as help command");
			}
			formatter.printHelp( "Main", options );
			return;
		}

		//
		// setup icons
		//
		primaryStage.getIcons().add(
    			new Image("images/mpu_icon_64.png")
    			);
    	
		//
		// load fxml and wire controllers
		//
    	FXMLLoader mainViewLoader= new FXMLLoader(getClass().getResource("mavenpomupdater.fxml"));
    	Parent mainView = (Parent)mainViewLoader.load();
    	MainViewController mainViewController = mainViewLoader.getController();
    	
    	FXMLLoader alertViewLoader = new FXMLLoader(getClass().getResource("alert.fxml"));
    	Parent alertView = (Parent)alertViewLoader.load();
    	
    	final AlertController alertController = alertViewLoader.getController();
    	mainViewController.alertController = alertController;
    	alertController.mainViewControllerRef = new WeakReference<MainViewController>(mainViewController);
    	
        mainView.getStyleClass().add("main-view-pane");
        
        //
        // add FlowPane, StackPane objects (defined in program and outside of 
        // FXML)
        //
        final FlowPane fp = new FlowPane();
        fp.setAlignment(Pos.CENTER);
        fp.getChildren().add( alertView );
        fp.getStyleClass().add("alert-background-pane");
        
        alertView.getStyleClass().add("alert-pane");
        
    	final StackPane sp = new StackPane();    	
        sp.getChildren().add( fp );  // initially hide the alert
        sp.getChildren().add( mainView );
        
        //
        // setup scene
        //
        Scene scene = new Scene(sp);
        scene.getStylesheets().add("com/bekwam/mavenpomupdater/mpu.css");
        
        scene.setOnKeyPressed(
        	keyEvent -> {
                KeyCode key = keyEvent.getCode();
                if (key == KeyCode.ESCAPE  && (sp.getChildren().get(1) == fp)) {
                	if( log.isDebugEnabled() ) {
                		log.debug("[ESCAPE]");
                	}
               		alertController.action();
                }
        	});
        
        //
        // setup stage
        //
        primaryStage.setTitle("Maven POM Version Updater");
        primaryStage.setScene(scene);
        
    	if( cmd.hasOption("hidpi") ) {
    		
    		if( log.isInfoEnabled() ) {
    			log.info("running in Hi-DPI display mode");
    		}
            primaryStage.setWidth(1920.0);
            primaryStage.setHeight(1080.0);
            
            mainViewController.adjustForHiDPI();
            
    	} else {
    		if( log.isInfoEnabled() ) {
    			log.info("running in normal display mode");
    		}
    		
    		primaryStage.setWidth(1280.0);
    		primaryStage.setHeight(720.0);
    	}
    	
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
