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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    	
    	primaryStage.getIcons().add(
    			new Image("images/mpu_icon_64.png")
    			);
    	
    	Application.Parameters params = getParameters();
    	
    	List<String> unnamedList = params.getUnnamed();
    	
    	if( log.isDebugEnabled() ) {
    		for( String arg : unnamedList ) {
    			log.debug("received arg " + arg);
    		}
    	}

    	final StackPane sp = new StackPane();
    	
    	FXMLLoader mainViewLoader= new FXMLLoader(getClass().getResource("mavenpomupdater.fxml"));
    	Parent mainView = (Parent)mainViewLoader.load();
    	MainViewController mainViewController = mainViewLoader.getController();
    	
    	FXMLLoader alertViewLoader = new FXMLLoader(getClass().getResource("alert.fxml"));
    	Parent alertView = (Parent)alertViewLoader.load();
    	
    	final AlertController alertController = alertViewLoader.getController();
    	mainViewController.alertController = alertController;
    	alertController.mainViewControllerRef = new WeakReference<MainViewController>(mainViewController);
    	
        mainView.getStyleClass().add("main-view-pane");
        
        final FlowPane fp = new FlowPane();
        fp.setAlignment(Pos.CENTER);
        fp.getChildren().add( alertView );
        fp.getStyleClass().add("alert-background-pane");
        
        alertView.getStyleClass().add("alert-pane");
        
        sp.getChildren().add( fp );  // initially hide the alert
        sp.getChildren().add( mainView );
        
        primaryStage.setTitle("Maven POM Version Updater");
        
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
        
        primaryStage.setScene(scene);
        
    	if( CollectionUtils.isNotEmpty(unnamedList) 
    			&& StringUtils.equalsIgnoreCase(unnamedList.get(0), "hidpi") ) {
    		
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
