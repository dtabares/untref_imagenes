package sample.tpFinal;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import sample.*;

public class LevelSetController {

    @FXML private AnchorPane imagePane;
    @FXML private CheckBox chkTwoCycle;
    @FXML private TextField txtError;

    private ImageUtilities imageUtilities;

    // Variables para contornos
    private Image image;
    private LevelSet levelSet;
    List<LevelSetObject> objectList;
    private int idCounter=1;

    // Variables de secuencia de imagenes
    public ImageSequence is;
    private int counter;

    // Variables para video
    Timeline timeline;
    private int framesCount=0;

    public void initialize() throws Exception{
        imageUtilities = new ImageUtilities();
        //Seteo variables de configuracion inicial
        chkTwoCycle.setSelected(true);
        txtError.setText("40");
        //Inicializo variables de secuencia de imagenes
        counter = 0;
        is = new ImageSequence();
        BufferedImage bimg = is.imageList.get(counter);
        WritableImage wimg = imageUtilities.readImage(bimg);
        counter ++;
        //Inicializo variables de Active Contours
        image = new Image(bimg);
        //image.convertToGreyDataMatrix();
        objectList = new LinkedList<>();
        //Muestro imagen en interfaz
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
        objectList.clear();
        idCounter=1;
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
        System.out.println("Counter: " + counter);
        BufferedImage bimg = imageUtilities.copyImageIntoAnother(this.image.getBufferedImage());
        //Calculo las condiciones iniciales para todos los objetos
        if(counter == 1){
            for (LevelSetObject o : objectList) {
                o.calculateObjectColor(image);
                o.generateLinAndLoutBasedOnObjectSelection(image);
            }
            this.levelSet = new LevelSet(this.image,this.objectList);
        }
        else {
            this.levelSet.updateImage(image);
        }
        System.out.println("Applying level set to frame " + framesCount );
        this.levelSet.apply(chkTwoCycle.isSelected(), Integer.parseInt(txtError.getText()));
        WritableImage wimg = imageUtilities.readImage(this.paintContoursReloaded());
        ImageView imageView = new ImageView(wimg);
        imagePane.getChildren().setAll(imageView);
        framesCount++;
    }

    @FXML public void play(){
        if (timeline!=null){
            timeline.stop();
        }
        framesCount = 0;
        //Calculo las condiciones iniciales para todos los objetos
        for (LevelSetObject o : objectList) {
            o.calculateObjectColor(image);
            o.generateLinAndLoutBasedOnObjectSelection(image);
        }
        this.levelSet = new LevelSet(this.image,this.objectList);
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), ev -> {
                BufferedImage bimg = is.imageList.get(framesCount);
                image = new Image(bimg);
                //image.convertToGreyDataMatrix();
                this.levelSet.updateImage(image);
                System.out.println("Applying level set to frame " + framesCount );
                this.levelSet.apply(chkTwoCycle.isSelected(), Integer.parseInt(txtError.getText()));
                WritableImage wimg = imageUtilities.readImage(this.paintContoursReloaded());
                ImageView imageView = new ImageView(wimg);
                imagePane.getChildren().setAll(imageView);
                framesCount++;
                counter++;
        }));
        timeline.setCycleCount(is.imageList.size());
        timeline.play();
    }

    @FXML public void stop(){
        if (timeline!=null){
            timeline.stop();
        }
    }

    @FXML public void objectSquare(){
            //Crea un nuevo Objeto y le pasa sus atributos de seleccion
            LevelSetObject o = new LevelSetObject();
            o.objectSelection = new ImageSelection();
            Rectangle selectionLine = new Rectangle();
            ImageView leftImageView = (ImageView) imagePane.getChildren().get(0);
            leftImageView.setOnMouseClicked(e -> {
                o.objectSelection.submitClickCoordinates((int) e.getX(), (int) e.getY());
                if (o.objectSelection.allCoordinatesSubmitted()) {
                    //Calculate 4 points
                    o.objectSelection.calculateWithAndHeight();
                    selectionLine.setX(o.objectSelection.getxOrigin());
                    selectionLine.setY(o.objectSelection.getyOrigin());
                    selectionLine.setWidth(o.objectSelection.getWidth());
                    selectionLine.setHeight(o.objectSelection.getHeight());
                    selectionLine.setFill(Color.TRANSPARENT);
                    selectionLine.setStroke(Color.RED);
                    selectionLine.setStrokeWidth(3);
                    this.imagePane.getChildren().add(selectionLine);
                    //objectSelection.reset();
                } else {
                    //Removes selection line
                    this.imagePane.getChildren().remove(selectionLine);
                }
            });
            //Agrega el objeto a la lista
            o.id = idCounter;
            objectList.add(o);
            idCounter++;
    }

    private BufferedImage paintContoursReloaded(){
        BufferedImage bimg = imageUtilities.copyImageIntoAnother(image.getBufferedImage());
            for (LevelSetObject o : this.objectList) {
                //Pinto lin
                for (Pixel p: o.lin){
                    bimg.setRGB(p.getX(),p.getY(), ColorUtilities.createRGB(255,0,255));
                }
                //Pinto lout
                for (Pixel p: o.lout) {
                    bimg.setRGB(p.getX(),p.getY(), ColorUtilities.createRGB(246,219,41));
                }
            }
            return bimg;
        }

}
