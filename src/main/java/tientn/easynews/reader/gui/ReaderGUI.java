package tientn.easynews.reader.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.text.Font;

// Simple Hello World JavaFX program
public class ReaderGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }
 
    // JavaFX entry point
    @Override
    public void start(Stage primaryStage) throws Exception {
        String message = "Hello World!";
        Button btnHello = new Button();
        btnHello.setText(message);
        btnHello.setFont(Font.font ("Verdana", 15));
 
        // A layout container for UI controls
        StackPane root = new StackPane();
        root.getChildren().add(btnHello);
 
        // Top level container for all view content
        Scene scene = new Scene(root, 300, 250);
 
        // primaryStage is the main top level window created by platform
        primaryStage.setTitle(message);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}