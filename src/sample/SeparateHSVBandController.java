package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;


import java.awt.image.BufferedImage;
import java.io.IOException;


public class SeparateHSVBandController {

    @FXML private AnchorPane leftPane;
    @FXML private AnchorPane centerPane;
    @FXML private AnchorPane rightPane;
    @FXML private Label label;
    private ImageUtilities imageUtilities;



    public void initialize()throws IOException {
        System.out.println("Starting Second Window...");
        imageUtilities = new ImageUtilities();

    }

    public void displayImages(BufferedImage leftImage, BufferedImage centerImage, BufferedImage rightImage)
    {
        displayImageInPane(leftImage, this.leftPane);
        displayImageInPane(centerImage, this.centerPane);
        displayImageInPane(rightImage, this.rightPane);
    }

    public void setLabel(String text)
    {
        this.label.setText(text);
    }


    private void displayImageInPane(BufferedImage bimg, AnchorPane pane){

        WritableImage wimg = imageUtilities.readImage(bimg);
        ImageView image = new ImageView(wimg);
        pane.getChildren().setAll(image);
    }
}
