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
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.BuilderFactory;
import javafx.util.Callback;

import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

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

		AbstractModule module = null;
    	if( runningAsJNLP() ) {
    		
    		if( log.isInfoEnabled() ) {
    			log.info("using jnlp module and jnlp favorites store");
    		}
    		module = new MPUJNLPModule();
    	} else {
    		
    		if( log.isInfoEnabled() ) {
    			log.info("using standalone module and in-memory favorites store");
    		}
        	module = new MPUStandaloneModule();
    	}

		//
		// setup google guice
		//
		final Injector injector = Guice.createInjector(module);
		
		BuilderFactory builderFactory = new JavaFXBuilderFactory();
		
		Callback<Class<?>, Object> guiceControllerFactory = new Callback<Class<?>, Object>() {
			@Override
			public Object call(Class<?> clazz) {
				return injector.getInstance(clazz);
			}
		};
		
		//
		// setup icons
		//
		primaryStage.getIcons().add(
    			new Image("images/mpu_icon_64.png")
    			);
    	
		//
		// load fxml and wire controllers
		//
    	FXMLLoader mainViewLoader= new FXMLLoader(getClass().getResource("mavenpomupdater.fxml"), null, builderFactory, guiceControllerFactory);
    	Parent mainView = (Parent)mainViewLoader.load();
    	MainViewController mainViewController = mainViewLoader.getController();
    	
    	FXMLLoader alertViewLoader = new FXMLLoader(getClass().getResource("alert.fxml"), null, builderFactory, guiceControllerFactory);
    	Parent alertView = (Parent)alertViewLoader.load();
    	
    	//
    	// i'm continuing this manual wiring to 1) accommodate a potential
    	// bi-directional reference problem and 2) to make sure that guice
    	// doesn't return different object for the main -> alert and alert ->
    	// main dependency injections
    	//
    	
    	final AlertController alertController = alertViewLoader.getController();
    	mainViewController.alertController = alertController;
    	alertController.mainViewControllerRef = new WeakReference<MainViewController>(mainViewController);
    	
        //
        // add FlowPane, StackPane objects (defined in program and outside of 
        // FXML)
        //
        final FlowPane fp = new FlowPane();
        fp.setAlignment(Pos.CENTER);
        fp.getChildren().add( alertView );
        fp.getStyleClass().add("alert-background-pane");
        
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
            primaryStage.setWidth(2560.0);
            primaryStage.setHeight(1440.0);
            primaryStage.setMinWidth(1920.0);
            primaryStage.setMinHeight(1080.0);
            
            mainViewController.adjustForHiDPI();
            
    	} else {
    		if( log.isInfoEnabled() ) {
    			log.info("running in normal display mode");
    		}
    		
    		primaryStage.setWidth(1280.0);
    		primaryStage.setHeight(800.0);
    		primaryStage.setMinWidth(1024.0);
    		primaryStage.setMinHeight(768.0);

    	}
    	
        primaryStage.show();
    }

    private boolean runningAsJNLP() {
    	try {
    		ServiceManager.lookup("javax.jnlp.BasicService"); 
    		return true;
    	} catch(UnavailableServiceException exc) {
    		return false;
    	}
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
