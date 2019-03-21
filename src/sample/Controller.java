package sample;

import javafx.scene.layout.BorderPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import javafx.fxml.FXML;


public class Controller extends BorderPane {

    File imageFile = null;

    @FXML public void closeApplication(){
        System.exit(0);
    }
}
