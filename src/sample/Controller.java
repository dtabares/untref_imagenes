package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class Controller extends BorderPane {

    private BufferedImage leftImage = null;
    private List<BufferedImage> bufferedImageList;
    private ImageUtilities imageUtilities;

    @FXML private AnchorPane leftPane;
    @FXML private AnchorPane rightPane;
    @FXML private Text txtBottom;

    public void initialize()throws IOException{
        System.out.println("Starting...");
        this.bufferedImageList = new LinkedList<>();
        this.imageUtilities = new ImageUtilities();
    }

    //Top Menu

    @FXML public BufferedImage openImageFile(){
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        BufferedImage bimg = null;
        try
        {
            fc.setTitle("Select Directory");
            File f = fc.showOpenDialog(browser);
            String fileExtension = this.imageUtilities.getImageExtension(f.getName());
            if(this.imageUtilities.isSupportedFormat(fileExtension))
            {
                switch(fileExtension)
                {
                    case "raw":
                        int width = Integer.valueOf(JOptionPane.showInputDialog(
                                null, "Width", "Insert Width",
                                JOptionPane.DEFAULT_OPTION));

                        int height = Integer.valueOf(JOptionPane.showInputDialog(
                                null, "Height", "Insert Height",
                                JOptionPane.DEFAULT_OPTION));
                        bimg = this.imageUtilities.openRawImage(f,width,height);
                        leftImage = bimg;
                        //wimg = this.imageUtilities.readRawImage(bimg,width,height);
                        break;
                    case "pgm":
                        bimg = this.imageUtilities.readPGM(f);
                        leftImage = bimg;
                        break;
                    default:
                        bimg = ImageIO.read(f);
                        leftImage = bimg;
                }
                this.displayImageInPane(bimg,leftPane);
                fc.setInitialDirectory(null);
            }
            else
            {
                Alerts.showAlert("La extension del archivo no esta soportada");
            }
        }
        catch (Exception e)
        {

            Alerts.showAlert(e.getMessage());
        }
        return bimg;
    }
    @FXML public void saveImageFile(){
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        if (leftImage != null)
        {
            try {
                fc.setTitle("Select File");
                File f = fc.showSaveDialog (browser);
                String ext = f.getName().substring(f.getName().lastIndexOf("."));
                this.imageUtilities.WriteImage(leftImage, f , ext.substring(1) );
            }
            catch (Exception e)
            {
                Alerts.showAlert(e.getMessage());
            }
            fc.setInitialDirectory(null);
        }
        else
            {
                Alerts.showAlert("There is no image to save");
            }
    }
    @FXML public void closeApplication(){
        System.exit(0);
    }
    @FXML public void undo(){
        if(!bufferedImageList.isEmpty()){
            bufferedImageList.remove(bufferedImageList.size()-1);
            if(!bufferedImageList.isEmpty()) {
                WritableImage wimg = imageUtilities.readImage(bufferedImageList.get(bufferedImageList.size()-1));
                ImageView image = new ImageView(wimg);
                rightPane.getChildren().setAll(image);
            }
            else
            {
                rightPane.getChildren().setAll();
            }
        }
        else
        {
            Alerts.showAlert("No hay nada para deshacer");
        }
    }
    @FXML public void redo(){}
    @FXML public void reset(){
        bufferedImageList = new LinkedList<>();
        rightPane.getChildren().setAll();
    }

    //Left Pane

    @FXML public void createImageWithCircle(){
        int width = 200;
        int height = 200;
        int rad = 50;
        Rectangle r = new Rectangle();
        r.setX(0);
        r.setY(0);
        r.setWidth(width);
        r.setHeight(height);
        r.setFill(Color.BLACK);
        Circle c = new Circle();
        c.setCenterX(width /2);
        c.setCenterY(height /2);
        c.setRadius(rad);
        c.setFill(Color.WHITE);
        leftPane.getChildren().setAll(r,c);
        WritableImage image = leftPane.snapshot(null, null);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        try {
            fc.setTitle("Select File");
            File f = fc.showSaveDialog (browser);
            String fileName = f.getName();
            String[] splittedName = fileName.split("\\.");
            String ext = splittedName[splittedName.length -1];
            this.imageUtilities.WriteImage(bImage, f , ext );
        }
        catch (Exception e)
        {
            Alerts.showAlert(e.getMessage());
        }
        fc.setInitialDirectory(null);
    }
    @FXML public void createImageWithSquare(){
        int width = 200;
        int height = 200;
        Rectangle background = new Rectangle();
        background.setX(0);
        background.setY(0);
        background.setWidth(width);
        background.setHeight(height);
        background.setFill(Color.BLACK);
        Rectangle square = new Rectangle();
        square.setX(50);
        square.setY(50);
        square.setWidth(100);
        square.setHeight(100);
        square.setFill(Color.WHITE);
        leftPane.getChildren().setAll(background,square);
        WritableImage image = leftPane.snapshot(null, null);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        try {
            fc.setTitle("Select File");
            File f = fc.showSaveDialog (browser);
            this.imageUtilities.WriteImage(bImage, f , this.imageUtilities.getImageExtension(f.getName()));
        }
        catch (Exception e)
        {
            Alerts.showAlert(e.getMessage());
        }
        fc.setInitialDirectory(null);
    }

        //Basics

        public BufferedImage imageAddition(){
            BufferedImage bimg = this.openImageFile();
            BufferedImage bimg2 = this.openImageFile();
            BufferedImage result = imageUtilities.imageAddition(bimg,bimg2);
            WritableImage wimg = imageUtilities.readImage(result);
            this.displayImageInPane(result,rightPane);
            return result;
        }
        public BufferedImage imageSubstraction(){
            BufferedImage bimg = this.openImageFile();
            BufferedImage bimg2 = this.openImageFile();
            BufferedImage result = imageUtilities.imageSubstraction(bimg,bimg2);
            this.displayImageInPane(result,rightPane);
            return result;
        }
        public BufferedImage imageScalarProduct(){
            BufferedImage bimg = this.openImageFile();
            int scalar = Integer.valueOf(JOptionPane.showInputDialog(
                    null, "Scalar", "Insert Scalar",
                    JOptionPane.DEFAULT_OPTION));
            BufferedImage result = imageUtilities.imageScalarProduct(bimg,scalar);
            WritableImage wimg = imageUtilities.readImage(result);
            this.displayImageInPane(result,rightPane);
            return result;
        }
        public BufferedImage dynamicRangeCompression(){
           int alpha=-1;
            BufferedImage bimg = this.openImageFile();
    //        alpha = Integer.valueOf(JOptionPane.showInputDialog(
    //                null, "Compression", "Insert Compression %",
    //                JOptionPane.DEFAULT_OPTION));
    //        while(alpha <= 0 || alpha > 100)
    //        {
    //            Alerts.showAlert("El valor debe estar entre 1 y 100");
    //            alpha = Integer.valueOf(JOptionPane.showInputDialog(
    //                    null, "Compression", "Insert Compression %",
    //                    JOptionPane.DEFAULT_OPTION));
    //            ;
    //        }
            BufferedImage result = imageUtilities.dynamicRangeCompression(bimg,4);
            WritableImage wimg = imageUtilities.readImage(result);
            this.displayImageInPane(result,rightPane);
            return result;
            //return null;
        }
        @FXML public BufferedImage imagePow(){
            int gamma=-1;
    //        alpha = Integer.valueOf(JOptionPane.showInputDialog(
    //                null, "Compression", "Insert Compression %",
    //                JOptionPane.DEFAULT_OPTION));
    //        while(alpha <= 0 || alpha > 100)
    //        {
    //            Alerts.showAlert("El valor debe estar entre 1 y 100");
    //            alpha = Integer.valueOf(JOptionPane.showInputDialog(
    //                    null, "Compression", "Insert Compression %",
    //                    JOptionPane.DEFAULT_OPTION));
    //            ;
    //        }
            BufferedImage result = imageUtilities.imagePow(leftImage,5);
            this.displayImageInPane(result,rightPane);
            return result;
        }
        @FXML public BufferedImage imageNegative(){
            BufferedImage result = null;
            if (leftImage != null) {
                result = imageUtilities.imageNegative(leftImage);
                WritableImage wimg = imageUtilities.readImage(result);
                this.displayImageInPane(result, rightPane);
            }
            else
            {
                Alerts.showAlert("No hay una imagen abierta");
            }
            return result;
        }
        @FXML public void getPixelInformation(){
        try{
            ImageView leftImageView = (ImageView) leftPane.getChildren().get(0);
                ImageView rightImageView = (ImageView) rightPane.getChildren().get(0);

                leftImageView.setOnMouseClicked(e -> {
                    System.out.println("Left Coordinates Info: ["+e.getX()+", "+e.getY()+"]");
                    String message = this.imageUtilities.getPixelInformation(leftImage,(int)e.getX(),(int)e.getY());
                    this.setBottomText(message);
                });
                if (rightImageView !=null) {
                    rightImageView.setOnMouseClicked(e -> {
                        System.out.println("Right Coordinates Info:[" + e.getX() + ", " + e.getY() + "]");
                        String message = this.imageUtilities.getPixelInformation(bufferedImageList.get(bufferedImageList.size()), (int) e.getX(), (int) e.getY());
                        this.setBottomText(message);
                    });
                }
                else{
                    Alerts.showAlert("No hay una imagen cargada en el panel derecho");
                }
            }
            catch(Exception e){
            Alerts.showAlert(e.getMessage());
            }
        }

    //Panels

    public void displayImageInPane(BufferedImage bimg, AnchorPane pane){
        if (pane == rightPane) {
            bufferedImageList.add(bimg);
        }
        WritableImage wimg = imageUtilities.readImage(bimg);
        ImageView image = new ImageView(wimg);
        pane.getChildren().setAll(image);
    }

    //General functions

    public void setBottomText(String string)
    {
        this.txtBottom.setText("   " + string);
    }
}
