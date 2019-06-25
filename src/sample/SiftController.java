package sample;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.awt.image.BufferedImage;

public class SiftController {

    @FXML private AnchorPane pane;
    @FXML private Text txtBottom;
    private ImageUtilities imageUtilities = new ImageUtilities();

    public void displayImageInPane(BufferedImage bimg){
        WritableImage wimg = imageUtilities.readImage(bimg);
        ImageView image = new ImageView(wimg);
        this.pane.getChildren().setAll(image);
        this.pane.setVisible(true);
    }

    public void setText(String text){
        this.txtBottom.setText(" Objeto Encontrado: " + text);
    }

    public void setPane(AnchorPane pane){
        this.pane = pane;
    }

}
