package sample;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class BorderDirectionController {

    @FXML private AnchorPane horizontalPane;
    @FXML private AnchorPane verticalPane;
    @FXML private AnchorPane diag135Pane;
    @FXML private AnchorPane diag45Pane;
    private ImageUtilities imageUtilities;



    public void initialize()throws IOException {
        imageUtilities = new ImageUtilities();
    }

    public void displayImages(BufferedImage horizontal, BufferedImage vertical, BufferedImage diag135, BufferedImage diag45)
    {
        displayImageInPane(horizontal, this.horizontalPane);
        displayImageInPane(vertical, this.verticalPane);
        displayImageInPane(diag135, this.diag135Pane);
        displayImageInPane(diag45, this.diag45Pane);
    }

    private void displayImageInPane(BufferedImage bimg, AnchorPane pane){

        WritableImage wimg = imageUtilities.readImage(bimg);
        ImageView image = new ImageView(wimg);
        pane.getChildren().setAll(image);
    }
}
