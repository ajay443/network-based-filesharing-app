package gui.jfx.examples;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by Ajay on 1/31/17.
 */
public class MainGuiHelloWorld  extends Application  implements EventHandler<ActionEvent> {
    Button indexServerBtn;
    Button peerServerBtn;
    Button lamdabtn;
    Scene scene1,scene2,scene3;
    Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        //primaryStage.setFullScreen(true);

        primaryStage.setTitle("Hello World");
        indexServerBtn = new Button("indexServerBtn");

        peerServerBtn = new Button("peerServerBtn");

        StackPane layout = new StackPane();
        layout.getChildren().add(indexServerBtn);
        layout.getChildren().add(peerServerBtn);
        indexServerBtn.setOnAction(this);

       /* // another way of handeling it
        peerServerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("peerServerBtn clicked "+peerServerBtn);
            }
        });

        // using lambday expression
       // lamdabtn.setOnAction(e->System.out.println("Lambda button is cliecked !!! "));
        lamdabtn.setOnAction(e->{
            System.out.println("Lambda button 1 is cliecked !!! ");
            System.out.println("Lambda button 2 is cliecked !!! ");
        });*/


        scene1 = new Scene(layout,300,544);
        scene2 = new Scene(layout,400,544);
        scene3 = new Scene(layout,200,544);
        primaryStage.setScene(scene1);

        primaryStage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        if(event.getSource() == indexServerBtn){
            System.out.println("indexServerBtn clicked"+indexServerBtn);
        }

        if(event.getSource() == peerServerBtn){
            primaryStage.setScene(scene2);
            System.out.println("peerServerBtn clicked "+peerServerBtn);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
