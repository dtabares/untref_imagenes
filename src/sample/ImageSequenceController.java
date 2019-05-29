package sample;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;

import java.util.concurrent.TimeUnit;

public class ImageSequenceController {

    @FXML private AnchorPane imagePane;

    public ImageSequence is;
    private ImageUtilities imageUtilities;
    private int counter;

    public void initialize() throws Exception{
        counter = 0;
        is = new ImageSequence();
        imageUtilities = new ImageUtilities();
        WritableImage wimg = imageUtilities.readImage(is.imageList.get(counter));
        counter ++;
        ImageView image = new ImageView(wimg);
        imagePane.getChildren().setAll(image);
    }

    @FXML public void reset(){
        counter = 0;
        WritableImage wimg = imageUtilities.readImage(is.imageList.get(counter));
        ImageView image = new ImageView(wimg);
        imagePane.getChildren().setAll(image);
        counter ++;
    }

    @FXML public void next(){
        if (counter < is.imageList.size()) {
            WritableImage wimg = imageUtilities.readImage(is.imageList.get(counter));
            ImageView image = new ImageView(wimg);
            imagePane.getChildren().setAll(image);
            counter++;
        }
    }

    @FXML public void apply(){
        ActiveContours activeContours = new ActiveContours();
    }

    @FXML public void play()throws Exception{
        reset();
        try {
            TimeUnit.SECONDS.sleep(1);
            for (int i = counter; i < is.imageList.size(); i++) {
                if (counter < is.imageList.size()) {
                    WritableImage wimg = imageUtilities.readImage(is.imageList.get(i));
                    ImageView image = new ImageView(wimg);
                    imagePane.getChildren().setAll(image);
                    counter++;
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        }
        catch (Exception e){
        }
    }
}
