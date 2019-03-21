package sample;

import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;


public class Controller extends BorderPane {

    File imageFile = null;

    @FXML GridPane Grid;
    @FXML ImageView LeftImage;

    public void initialize(){
        System.out.println("Inicializando...");
    }

    @FXML public void openFile() {
        Stage directorioStage = null;
        FileChooser fc = new FileChooser();
        try {
            fc.setTitle("Seleccionar Directorio");
            if (imageFile!=null){
                fc.setInitialDirectory(imageFile);
            }
            imageFile = fc.showOpenDialog(directorioStage);
            if(imageFile.getName().toLowerCase().contains(".raw") ||
                    imageFile.getName().toLowerCase().contains(".ppm") ||
                        imageFile.getName().toLowerCase().contains(".pgm") ||
                            imageFile.getName().toLowerCase().contains(".jpg") ||
                                imageFile.getName().toLowerCase().contains(".png")
            ) {
                imageFile = new File(imageFile.getAbsolutePath());
                System.out.println(imageFile.getAbsolutePath());
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("La extension del archivo no esta soportada");
                alert.showAndWait();
            }
            if(imageFile != null){
                Grid = new GridPane();
                Image img = new Image("file:"+imageFile.getAbsolutePath());
                LeftImage = new ImageView();
                LeftImage.setImage(img);
                Grid.getChildren().setAll();
            }
        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            imageFile = null;
        }
        fc = null;
        directorioStage = null;
    }
    @FXML public void closeApplication(){
        System.exit(0);
    }
}
