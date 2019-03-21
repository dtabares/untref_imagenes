package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("root_scene.fxml"));
        Scene scene = new Scene(borderPane);
        primaryStage.setTitle("Imagenes");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
