package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
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
import javafx.scene.image.WritableImage;

public class Controller extends BorderPane {

    private File imageFile = null;
    private BufferedImage leftImage = null;
    private BufferedImage rightImage = null;
    private ImageUtilities imageUtilities;


    @FXML private AnchorPane leftPane;
    @FXML private AnchorPane rightPane;

    @FXML private Text txtBottom;

    public void initialize()throws IOException{
        System.out.println("Starting...");
        this.imageUtilities = new ImageUtilities();
        this.setBottomText("Mensaje de prueba, remover del medotod initialize en Controller");
    }

    public void setBottomText(String string)
    {
        this.txtBottom.setText("   " + string);
    }

    @FXML public void openImageFile()
    {
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        WritableImage wimg = null;
        BufferedImage bimg = null;
        try
        {
            fc.setTitle("Select Directory");
            File f = fc.showOpenDialog(browser);
            String fileExtension = this.imageUtilities.getImageExtension(f.getName());
            if(this.imageUtilities.isSupportedFormat(fileExtension))
            {
                imageFile = new File(f.getAbsolutePath());
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
                        leftImage = rightImage = bimg;
                        wimg = this.imageUtilities.readRawImage(bimg,width,height);
                        break;
                    case "pgm":
                        bimg = this.imageUtilities.readPGMNew(imageFile);
                        leftImage = rightImage = bimg;
                        wimg = this.imageUtilities.readImage(bimg);
                        break;
                    default:
                        bimg = ImageIO.read(f);
                        leftImage = rightImage = bimg;
                        wimg = this.imageUtilities.readImage(bimg);
                }
                displayImageInPane(wimg, leftPane);
                displayImageInPane(wimg, rightPane);
                fc.setInitialDirectory(null);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("La extension del archivo no esta soportada");
                alert.showAndWait();
            }
        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            imageFile = null;
        }
    }



    @FXML public void saveImageFile()
    {
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
                ShowAlert(e.getMessage());
            }
            fc.setInitialDirectory(null);
        }
        else
            {
                ShowAlert("There is no image to save");
            }
    }

    @FXML public void closeApplication(){
        System.exit(0);
    }

    public void displayImageInPane(WritableImage wimg, AnchorPane pane)
    {
        ImageView image = new ImageView(wimg);
        pane.getChildren().setAll(image);
    }


    public void ShowAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML public void createImageWithCircle()
    {
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
            ShowAlert(e.getMessage());
        }
        fc.setInitialDirectory(null);
    }

    @FXML public void createImageWithSquare()
    {
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
            ShowAlert(e.getMessage());
        }
        fc.setInitialDirectory(null);
    }

    @FXML public void getPixelInformation()
    {
        ImageView leftImageView = (ImageView) leftPane.getChildren().get(0);
        ImageView rightImageView = (ImageView) rightPane.getChildren().get(0);


        leftImageView.setOnMouseClicked(e -> {
            System.out.println("Left Coordinates Info: ["+e.getX()+", "+e.getY()+"]");
            String message = this.imageUtilities.getPixelInformation(leftImage,(int)e.getX(),(int)e.getY());
            this.setBottomText(message);
        });

        rightImageView.setOnMouseClicked(e -> {
            System.out.println("Right Coordinates Info:["+e.getX()+", "+e.getY()+"]");
            String message = this.imageUtilities.getPixelInformation(rightImage,(int)e.getX(),(int)e.getY());
            this.setBottomText(message);
        });
    }

}
