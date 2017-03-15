/*
 * Copyright (C) 2017.  FileSharingSystem - https://github.com/ajayramesh23/FileSharingSystem
 * Programming Assignment from Professor Z.Lan
 * @author Ajay Ramesh
 * @author Chandra Kumar Basavaraj
 * Last Modified - 3/15/17 6:44 PM
 */

package gui.jfx.examples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Created by Ajay on 2/2/17.
 */
public class MainSwitchScene  extends Application{
    Stage window;
    Scene  scene1,scene2,scene3;
    Button indexServer, button1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        Label label1 = new Label("Index Server is on ");

        button1 = new Button("button1  Server Mode");
        button1.setOnAction(e -> window.setScene(scene2));

        VBox layout1 = new VBox(20);
        layout1.getChildren().addAll(label1, button1);
        scene1 = new Scene(layout1,200,300);
        window.setScene(scene1);
        window.show();

        indexServer = new Button("Index  Server Mode");

/*
        VBox layout2 = new VBox(20);
        layout2.getChildren().addAll(button1);
        scene2 = new Scene(layout2,200,300);
        window.setTitle("Test");
        indexServer.setOnAction(e -> window.setScene(scene1));*/


    }
}
