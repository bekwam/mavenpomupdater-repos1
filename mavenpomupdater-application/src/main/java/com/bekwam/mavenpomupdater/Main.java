package com.bekwam.mavenpomupdater;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;
import javafx.stage.Stage;

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
	
    @Override
    public void start(Stage primaryStage) throws Exception{
    	
    	final StackPane sp = StackPaneBuilder.create().build();
    	
    	FXMLLoader mainViewLoader= new FXMLLoader(getClass().getResource("mavenpomupdater.fxml"));
    	Parent mainView = (Parent)mainViewLoader.load();
    	MainViewController mainViewController = mainViewLoader.getController();
    	
    	FXMLLoader alertViewLoader = new FXMLLoader(getClass().getResource("alert.fxml"));
    	Parent alertView = (Parent)alertViewLoader.load();
    	
    	final AlertController alertController = alertViewLoader.getController();
    	mainViewController.alertController = alertController;

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
        
        scene.setOnKeyPressed( new EventHandler<KeyEvent>() {
        	 @Override
             public void handle(KeyEvent t) {
                 KeyCode key = t.getCode();
                 if (key == KeyCode.ESCAPE  && (sp.getChildren().get(1) == fp)) {
                	 if( log.isDebugEnabled() ) {
                		 log.debug("[KEY] the escape key was pressed on the alert screen");
                		 alertController.dismiss();
                	 }
                 }
             }
        });
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
