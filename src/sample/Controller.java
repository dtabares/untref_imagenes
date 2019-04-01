package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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

import static sample.InterfaceHelper.getInputDialog;


public class Controller extends BorderPane {

    private BufferedImage leftImage = null;
    private List<BufferedImage> rightPaneImageList;
    private List<BufferedImage> historyImageList;
    private ImageUtilities imageUtilities;

    @FXML private AnchorPane leftPane;
    @FXML private AnchorPane rightPane;
    @FXML private Text txtBottom;

    public void initialize()throws IOException{
        System.out.println("Starting...");
        this.rightPaneImageList = new LinkedList<>();
        this.historyImageList = new LinkedList<>();
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
                        int width = Integer.valueOf(getInputDialog("Open Image","Raw Image Information","Insert Image Width"));
                        int height = Integer.valueOf(getInputDialog("Open Image","Raw Image Information","Insert Image Height"));
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
        BufferedImage imageToBeSaved = this.getLastModifiedImage();
        if (imageToBeSaved != null)
        {
            try {
                fc.setTitle("Select File");
                File f = fc.showSaveDialog (browser);
                this.imageUtilities.WriteImage(imageToBeSaved, f);
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
        if(!rightPaneImageList.isEmpty()){
            historyImageList.add(rightPaneImageList.get(rightPaneImageList.size()-1));
            rightPaneImageList.remove(rightPaneImageList.size()-1);
            if(!rightPaneImageList.isEmpty()) {
                WritableImage wimg = imageUtilities.readImage(rightPaneImageList.get(rightPaneImageList.size()-1));
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
    @FXML public void redo(){
        if(!historyImageList.isEmpty())
        {
            rightPaneImageList.add(historyImageList.get(historyImageList.size()-1));
            historyImageList.remove(historyImageList.get(historyImageList.size()-1));
            WritableImage wimg = imageUtilities.readImage(rightPaneImageList.get(rightPaneImageList.size()-1));
            ImageView image = new ImageView(wimg);
            rightPane.getChildren().setAll(image);
        }
        else{
            Alerts.showAlert("No hay nada para rehacer");
        }
    }
    @FXML public void reset(){
        rightPaneImageList = new LinkedList<>();
        historyImageList = new LinkedList<>();
        rightPane.getChildren().setAll();
    }
    @FXML public void setRightToLeft(){
        if(!rightPaneImageList.isEmpty()) {
            leftImage = rightPaneImageList.get(rightPaneImageList.size()-1);
            rightPaneImageList.remove(rightPaneImageList.size()-1);
            this.reset();
            this.displayImageInPane(leftImage,leftPane);
        }
        else{
            Alerts.showAlert("No hay imagen para mover");
        }
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
            this.imageUtilities.WriteImage(bImage, f);
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
            this.imageUtilities.WriteImage(bImage, f);
        }
        catch (Exception e)
        {
            Alerts.showAlert(e.getMessage());
        }
        fc.setInitialDirectory(null);
    }

    //Basics

    public BufferedImage imageAddition(){
        BufferedImage temp = leftImage;
        BufferedImage bimg = this.openImageFile();
        leftImage = temp;
        BufferedImage result = imageUtilities.imageAddition(leftImage,bimg);
        this.displayImageInPane(result,rightPane);
        this.displayImageInPane(leftImage, leftPane);
        return result;
    }
    public BufferedImage imageSubtraction(){
        BufferedImage temp = leftImage;
        BufferedImage bimg = this.openImageFile();
        leftImage = temp;
        BufferedImage result = imageUtilities.imageSubtraction(leftImage,bimg);
        this.displayImageInPane(result,rightPane);
        this.displayImageInPane(leftImage, leftPane);
        return result;
    }
    public BufferedImage imageScalarProduct(){
        int scalar = Integer.valueOf(JOptionPane.showInputDialog(
                null, "Scalar", "Insert Scalar",
                JOptionPane.DEFAULT_OPTION));
        BufferedImage result = imageUtilities.imageScalarProduct(leftImage,scalar);
        this.displayImageInPane(result,rightPane);
        return result;
    }
    public BufferedImage dynamicRangeCompression(){
        int alpha=-1;
        alpha = Integer.valueOf(JOptionPane.showInputDialog(
                null, "Compression", "Insert Compression %",
                JOptionPane.DEFAULT_OPTION));
        while(alpha <= 0 || alpha > 100)
        {
            Alerts.showAlert("El valor debe estar entre 1 y 100");
            alpha = Integer.valueOf(JOptionPane.showInputDialog(
                    null, "Compression", "Insert Compression %",
                    JOptionPane.DEFAULT_OPTION));
            ;
        }
        BufferedImage result = imageUtilities.dynamicRangeCompression(leftImage,alpha);
        this.displayImageInPane(result,rightPane);
        return result;
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
            ImageView leftImageView = null;
            ImageView rightImageView = null;
            if (!leftPane.getChildren().isEmpty())
            {
                leftImageView = (ImageView) leftPane.getChildren().get(0);
            }

            if (!rightPane.getChildren().isEmpty())
            {
                rightImageView = (ImageView) rightPane.getChildren().get(0);
            }

            if (leftImageView == null && rightImageView == null)
            {
                Alerts.showAlert("No hay una imagen cargada!");
            }
            else
            {
                if (leftImageView != null)
                {
                    leftImageView.setOnMouseClicked(e -> {
                        System.out.println("Left Coordinates Info: ["+e.getX()+", "+e.getY()+"]");
                        String message = this.imageUtilities.getPixelInformation(leftImage,(int)e.getX(),(int)e.getY());
                        this.setBottomText(message);
                    });
                }


                if (rightImageView != null) {
                    rightImageView.setOnMouseClicked(e -> {
                        System.out.println("Right Coordinates Info:[" + e.getX() + ", " + e.getY() + "]");
                        String message = this.imageUtilities.getPixelInformation(rightPaneImageList.get(rightPaneImageList.size()), (int) e.getX(), (int) e.getY());
                        this.setBottomText(message);
                    });
                }
            }

        }
        catch(Exception e){
            Alerts.showAlert(e.getMessage());
        }
    }

    @FXML public void modifyPixelInformation()
    {
        try{
            ImageView leftImageView = null;
            ImageView rightImageView = null;
            if (!leftPane.getChildren().isEmpty())
            {
                leftImageView = (ImageView) leftPane.getChildren().get(0);
            }

            if (!rightPane.getChildren().isEmpty())
            {
                rightImageView = (ImageView) rightPane.getChildren().get(0);
            }

            if (leftImageView == null && rightImageView == null)
            {
                Alerts.showAlert("No hay una imagen cargada!");
            }
            else
            {
                int red = Integer.valueOf(getInputDialog("Modify Pixel Information", "Enter a new Value", "Red:"));
                int green = Integer.valueOf(getInputDialog("Modify Pixel Information", "Enter a new Value", "Green:"));
                int blue = Integer.valueOf(getInputDialog("Modify Pixel Information", "Enter a new Value", "Blue:"));
                if (leftImageView != null)
                {
                    leftImageView.setOnMouseClicked(e -> {
                        System.out.println("Left Coordinates Info: ["+e.getX()+", "+e.getY()+"]");
                        BufferedImage modifiedImage = this.imageUtilities.modifyPixelInformation(leftImage,(int)e.getX(),(int)e.getY(),red,green,blue);
                        this.displayImageInPane(modifiedImage,rightPane);
                    });
                }


                if (rightImageView != null) {
                    rightImageView.setOnMouseClicked(e -> {
                        System.out.println("Right Coordinates Info:[" + e.getX() + ", " + e.getY() + "]");
                        BufferedImage modifiedImage = this.imageUtilities.modifyPixelInformation(rightPaneImageList.get(rightPaneImageList.size()), (int) e.getX(), (int) e.getY(),red,green,blue);
                        this.displayImageInPane(modifiedImage,rightPane);
                    });
                }
            }
        }
        catch(Exception e){
            Alerts.showAlert(e.getMessage());
        }
    }

    @FXML public void copyImageSelection()
    {
        try{
            ImageView leftImageView = null;
            if (!leftPane.getChildren().isEmpty())
            {
                leftImageView = (ImageView) leftPane.getChildren().get(0);
            }
            if (leftImageView == null)
            {
                Alerts.showAlert("No hay una imagen cargada!");
            }
            else
            {
                ImageSelection selection = new ImageSelection();
                leftImageView.setOnMouseClicked(e -> {
                    System.out.println("Left Coordinates Info: ["+e.getX()+", "+e.getY()+"]");
                    selection.submitClickCoordinates((int)e.getX(), (int) e.getY());

                    if(selection.allCoordinatesSubmitted())
                    {
                        //Calculate 4 points
                        selection.calculateWithAndHeight();

                        //Create new buffered image from those 4 points
                        BufferedImage imageSelection = leftImage.getSubimage(selection.getxOrigin(),selection.getyOrigin(),selection.getWidth(),selection.getHeight());

                        // Display it on the right pane and add it to the list

                        this.displayImageInPane(imageSelection,rightPane);
                        selection.reset();
                    }
                });
            }
        }
        catch(Exception e){
            Alerts.showAlert(e.getMessage());
        }
    }

    //Panels

    public void displayImageInPane(BufferedImage bimg, AnchorPane pane){
        if (pane == rightPane) {
            rightPaneImageList.add(bimg);
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

    private BufferedImage getLastModifiedImage()
    {
        BufferedImage lastModifiedImage = null;
        if (rightPaneImageList.size() > 0)
        {
            lastModifiedImage = rightPaneImageList.get(rightPaneImageList.size() - 1);
        }
        return lastModifiedImage;

    }
}
