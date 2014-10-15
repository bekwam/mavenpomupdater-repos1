package com.bekwam.mavenpomupdater;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.stage.Stage;

/**
 * Main entry point for MPU
 * 
 * @author carlwalker
 * @since 1.1.0
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
    	
    	StackPane sp = StackPaneBuilder.create().build();
    	
    	FXMLLoader mainViewLoader= new FXMLLoader(getClass().getResource("mavenpomupdater.fxml"));
    	Parent mainView = (Parent)mainViewLoader.load();
    	MainViewController mainViewController = mainViewLoader.getController();
    	
    	FXMLLoader alertViewLoader = new FXMLLoader(getClass().getResource("alert.fxml"));
    	Parent alertView = (Parent)alertViewLoader.load();
    	
    	AlertController alertController = alertViewLoader.getController();
    	mainViewController.alertController = alertController;
    	
        mainView.getStyleClass().add("main-view-pane");
        
        FlowPane fp = new FlowPane();
        fp.setAlignment(Pos.CENTER);
        fp.getChildren().add( alertView );
        fp.getStyleClass().add("alert-background-pane");
        
        alertView.getStyleClass().add("alert-pane");
        
        sp.getChildren().add( fp );  // initially hide the alert
        sp.getChildren().add( mainView );
        
        primaryStage.setTitle("Maven POM Version Updater");
        
        Scene scene = new Scene(sp);
        scene.getStylesheets().add("com/bekwam/mavenpomupdater/mpu.css");
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
