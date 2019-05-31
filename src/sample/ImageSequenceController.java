package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImageSequenceController {

    @FXML private AnchorPane imagePane;

    public ImageSequence is;
    private ImageUtilities imageUtilities;
    private int counter;
    private Image image;
    private ImageSelection objectSelection;
    private ImageSelection backgroundSelection;
    private List<Pixel> lin;
    private List<Pixel> lout;
    int [][] phiMatrix;
    private double backgroundTheta;
    private double objectTheta;

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
            this.backgroundTheta = this.calculateTheta(this.backgroundSelection);
            this.objectTheta = this.calculateTheta(this.objectSelection);
            this.generateLinAndLoutBasedOnObjectSelection();
            activeContours = new ActiveContours(this.image, lin,lout,objectTheta);

        }
        //Estoy en un frame > 1
        else {
            activeContours = new ActiveContours(this.image, lin,lout,objectTheta,this.phiMatrix);
        }
        activeContours.apply();
        this.lin = activeContours.getLin();
        this.lout = activeContours.getLout();
        this.phiMatrix = activeContours.getPhiMatrix();
        WritableImage wimg = imageUtilities.readImage(activeContours.paintContours());
        ImageView imageView = new ImageView(wimg);
        imagePane.getChildren().setAll(imageView);
    }

    @FXML public void play()throws Exception{
        reset();
        try {
            TimeUnit.SECONDS.sleep(1);
            for (int i = counter; i < is.imageList.size(); i++) {
                if (counter < is.imageList.size()) {
                    WritableImage wimg = imageUtilities.readImage(is.imageList.get(i));
                    ImageView imageView = new ImageView(wimg);
                    imagePane.getChildren().setAll(imageView);
                    counter++;
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        }
        catch (Exception e){
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

    private double calculateTheta(ImageSelection selection){
        double theta = 0.0;

        int[][] imageDataMatrix = this.image.getGreyDataMatrix();
        for (int i = selection.getxOrigin(); i <= selection.getxFinal(); i++) {
            for (int j = selection.getyOrigin(); j <= selection.getyFinal(); j++) {
                theta += (double) imageDataMatrix[i][j];
            }
        }

        theta = theta /(double) selection.getSize();
        System.out.println("Theta: " + theta);

        return theta;
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
