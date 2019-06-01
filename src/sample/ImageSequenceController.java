package sample;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class ImageSequenceController extends JFrame{

    @FXML private AnchorPane imagePane;

    public ImageSequence is;
    private ImageUtilities imageUtilities;
    private int counter;
    private int objectColor;
    private Image image;
    private ImageSelection objectSelection;
    private ImageSelection backgroundSelection;
    private List<Pixel> lin;
    private List<Pixel> lout;
    int [][] phiMatrix;
    private double backgroundTheta;
    private double objectTheta;

    //variables para video
    private Timer sequenceTimer = new Timer();
    private Timer timeTimer = new Timer();
    private TimerTask sequenceTask;
    private TimerTask timeTask;
    private int secondsCount=0;
    private int framesCount=0;

    public void initialize() throws Exception{
        counter = 0;
        is = new ImageSequence();
        imageUtilities = new ImageUtilities();
        WritableImage wimg = imageUtilities.readImage(is.imageList.get(counter));
        //Lo transforme a buffered image porque lo necesito asi para crear nuestra imagen en formato matriz
        BufferedImage bimg = SwingFXUtils.fromFXImage(wimg, null);
        image = new Image(bimg);
        image.convertToGreyDataMatrix();
        counter ++;
        ImageView imageView = new ImageView(wimg);
        imagePane.getChildren().setAll(imageView);
    }

    @FXML public void reset(){
        counter = 0;
        WritableImage wimg = imageUtilities.readImage(is.imageList.get(counter));
        BufferedImage bimg = SwingFXUtils.fromFXImage(wimg, null);
        image = new Image(bimg);
        image.convertToGreyDataMatrix();
        ImageView imageView = new ImageView(wimg);
        imagePane.getChildren().setAll(imageView);
        counter ++;
    }

    @FXML public void next(){
        if (counter < is.imageList.size()) {
            WritableImage wimg = imageUtilities.readImage(is.imageList.get(counter));
            BufferedImage bimg = SwingFXUtils.fromFXImage(wimg, null);
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
        sequenceTask = new TimerTask(){
            public void run() {
                if(framesCount < is.imageList.size()){
                    WritableImage wimg = imageUtilities.readImage(is.imageList.get(framesCount));
                    ImageView imageView = new ImageView(wimg);
                    Platform.runLater(() -> imagePane.getChildren().setAll(imageView));
                    framesCount ++;
                }
                else{
                    this.cancel();
                }
            }
        };

        //running timer task as daemon thread
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(sequenceTask, 0, 10*1000);
        System.out.println("TimerTask started");
        //cancel after sometime
        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        timer.cancel();
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

    private int calculateObjectColor(ImageSelection selection){

        int red = 0;
        int green = 0;
        int blue = 0;
        int p;

        for (int i = selection.getxOrigin(); i <= selection.getxFinal(); i++) {
            for (int j = selection.getyOrigin(); j <= selection.getyFinal(); j++) {
                p = this.image.getBufferedImage().getRGB(i,j);
                red += ColorUtilities.getRed(p);
                green += ColorUtilities.getGreen(p);
                blue += ColorUtilities.getBlue(p);
            }
        }

        red = red /selection.getSize();
        green = green /selection.getSize();
        blue = blue /selection.getSize();

        p = ColorUtilities.createRGB(red,green,blue);

        return p;
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
