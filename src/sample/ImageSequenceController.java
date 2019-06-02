package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ImageSequenceController extends JFrame{

    @FXML private AnchorPane imagePane;

    public ImageSequence is;
    private ImageUtilities imageUtilities;
    private int counter;
    private int[] objectColor;
    private Image image;
    private ImageSelection objectSelection;
    private ImageSelection backgroundSelection;
    private List<Pixel> lin;
    private List<Pixel> lout;
    int [][] phiMatrix;
    private double backgroundTheta;
    private double objectTheta;

    //variables para video
    Timeline timeline;
    private int framesCount=0;

    public void initialize() throws Exception{
        counter = 0;
        is = new ImageSequence();
        imageUtilities = new ImageUtilities();
        BufferedImage bimg = is.imageList.get(counter);
        WritableImage wimg = imageUtilities.readImage(bimg);
        image = new Image(bimg);
        image.convertToGreyDataMatrix();
        counter ++;
        ImageView imageView = new ImageView(wimg);
        imagePane.getChildren().setAll(imageView);
    }

    @FXML public void reset(){
        counter = 0;
        BufferedImage bimg = is.imageList.get(counter);
        WritableImage wimg = imageUtilities.readImage(bimg);
        image = new Image(bimg);
        image.convertToGreyDataMatrix();
        ImageView imageView = new ImageView(wimg);
        imagePane.getChildren().setAll(imageView);
        counter ++;
        framesCount = 0;
    }

    @FXML public void next(){
        if (counter < is.imageList.size()) {
            BufferedImage bimg = is.imageList.get(counter);
            WritableImage wimg = imageUtilities.readImage(bimg);
            image = new Image(bimg);
            image.convertToGreyDataMatrix();
            ImageView imageView = new ImageView(wimg);
            imagePane.getChildren().setAll(imageView);
            counter++;
        }
    }

    @FXML public void apply(){
        ActiveContours activeContours;
        //Si estoy en el primer frame
        if(counter == 1){
            this.objectColor = this.calculateObjectColor(this.objectSelection);
            this.generateLinAndLoutBasedOnObjectSelection();
            activeContours = new ActiveContours(this.image, lin,lout,objectColor);
        }
        //Estoy en un frame > 1
        else {
            activeContours = new ActiveContours(this.image, lin,lout,objectColor,this.phiMatrix);
        }
        activeContours.applyReloaded();
        this.lin = activeContours.getLin();
        this.lout = activeContours.getLout();
        this.phiMatrix = activeContours.getPhiMatrix();
        WritableImage wimg = imageUtilities.readImage(activeContours.paintContours());
        ImageView imageView = new ImageView(wimg);
        imagePane.getChildren().setAll(imageView);
    }

    @FXML public void play()throws Exception{
        reset();
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), ev -> {
            if (framesCount < is.imageList.size()) {
                WritableImage wimg = imageUtilities.readImage(is.imageList.get(framesCount));
                ImageView imageView = new ImageView(wimg);
                System.out.println("Processing frame " + framesCount);
                imagePane.getChildren().setAll(imageView);
                framesCount++;
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @FXML public void stop(){
        if (timeline!=null){
            timeline.stop();
        }
    }

    @FXML public void objectSquare(){
        objectSelection = new ImageSelection();
        Rectangle selectionLine = new Rectangle();
        ImageView leftImageView = (ImageView) imagePane.getChildren().get(0);
        leftImageView.setOnMouseClicked(e -> {
            objectSelection.submitClickCoordinates((int)e.getX(), (int) e.getY());

            if(objectSelection.allCoordinatesSubmitted())
            {
                //Calculate 4 points
                objectSelection.calculateWithAndHeight();
                selectionLine.setX(objectSelection.getxOrigin());
                selectionLine.setY(objectSelection.getyOrigin());
                selectionLine.setWidth(objectSelection.getWidth());
                selectionLine.setHeight(objectSelection.getHeight());
                selectionLine.setFill(Color.TRANSPARENT);
                selectionLine.setStroke(Color.RED);
                selectionLine.setStrokeWidth(3);


                this.imagePane.getChildren().add(selectionLine);
                //objectSelection.reset();
            }
            else {
                //Removes selection line
                this.imagePane.getChildren().remove(selectionLine);
            }
        });
    }

    @FXML public void backgroundSquare(){
        backgroundSelection = new ImageSelection();
        Rectangle selectionLine = new Rectangle();
        ImageView leftImageView = (ImageView) imagePane.getChildren().get(0);
        leftImageView.setOnMouseClicked(e -> {
            backgroundSelection.submitClickCoordinates((int)e.getX(), (int) e.getY());

            if(backgroundSelection.allCoordinatesSubmitted())
            {
                //Calculate 4 points
                backgroundSelection.calculateWithAndHeight();
                selectionLine.setX(backgroundSelection.getxOrigin());
                selectionLine.setY(backgroundSelection.getyOrigin());
                selectionLine.setWidth(backgroundSelection.getWidth());
                selectionLine.setHeight(backgroundSelection.getHeight());
                selectionLine.setFill(Color.TRANSPARENT);
                selectionLine.setStroke(Color.BLUE);
                selectionLine.setStrokeWidth(3);


                this.imagePane.getChildren().add(selectionLine);
                //objectSelection.reset();
            }
            else {
                //Removes selection line
                this.imagePane.getChildren().remove(selectionLine);
            }
        });
    }

    private int[] calculateObjectColor(ImageSelection selection){

        int red = 0;
        int green = 0;
        int blue = 0;
        int[] rgb = new int[3];
        int p;
        int counter = 0;

        for (int i = selection.getxOrigin(); i <= selection.getxFinal(); i++) {
            for (int j = selection.getyOrigin(); j <= selection.getyFinal(); j++) {
                p = this.image.getBufferedImage().getRGB(i,j);
                red += ColorUtilities.getRed(p);
                green += ColorUtilities.getGreen(p);
                blue += ColorUtilities.getBlue(p);
                counter++;
            }
        }

        rgb[0] = red /counter;
        rgb[1] = green /counter;
        rgb[2] = blue /counter;


        return rgb;
    }


    private void generateLinAndLoutBasedOnObjectSelection(){
        lin = new LinkedList<>();
        lout = new LinkedList<>();
        int[][] imageDataMatrix = this.image.getGreyDataMatrix();

        //Genero Lin
        for (int i = this.objectSelection.getxOrigin(); i <= this.objectSelection.getxFinal(); i++) {
            for (int j = this.objectSelection.getyOrigin(); j <= this.objectSelection.getyFinal(); j++) {

                if (j == this.objectSelection.getyOrigin() || j == this.objectSelection.getyFinal()){
                    Pixel p = new Pixel(i,j,imageDataMatrix[i][j]);
                    lin.add(p);
                }
                else{
                    if(i == this.objectSelection.getxOrigin() || i == this.objectSelection.getxFinal()){
                        Pixel p = new Pixel(i,j,imageDataMatrix[i][j]);
                        lin.add(p);
                    }
                }


            }
        }

        //Genero Lout
        int loutXOrigin = this.objectSelection.getxOrigin() - 1;
        int loutXFinal = this.objectSelection.getxFinal() + 1;
        int loutYOrigin = this.objectSelection.getyOrigin() - 1;
        int loutYFinal = this.objectSelection.getyFinal() + 1;

        for (int i = loutXOrigin; i <= loutXFinal; i++) {
            for (int j = loutYOrigin; j <= loutYFinal; j++) {
                if (j == loutYOrigin || j == loutYFinal){
                    Pixel p = new Pixel(i,j,imageDataMatrix[i][j]);
                    lout.add(p);
                }
                else{
                    if(i == loutXOrigin || i == loutXFinal){
                        Pixel p = new Pixel(i,j,imageDataMatrix[i][j]);
                        lout.add(p);
                    }
                }
            }
        }
    }
}
