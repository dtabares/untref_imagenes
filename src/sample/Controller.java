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
    
    public boolean isRawFormat(File f)
    {
        if (f.getName().toLowerCase().contains("raw"))
        {
            return true;
        }
        return false;
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
                        wimg = this.imageUtilities.readRawImage(bimg,width,height);
                        break;
                    case "pgm":
                        bimg = this.imageUtilities.readPGM(imageFile);
                        wimg = this.imageUtilities.readImage(bimg);
                        break;
                    default:
                        bimg = ImageIO.read(f);
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

    public void mouseDragged(MouseEvent evt) {

/* Esta funcion puede servir para la funcionalidad de seleccionar una parte de la imagen...

if (dragging == false)
            return;  // Nothing to do because the user isn't drawing.

        double x = evt.getX();   // x-coordinate of mouse.
        double y = evt.getY();   // y-coordinate of mouse.

        if (x < 3)                          // Adjust the value of x,
            x = 3;                           //   to make sure it's in
        if (x > canvas.getWidth() - 57)       //   the drawing area.
            x = (int)canvas.getWidth() - 57;

        if (y < 3)                          // Adjust the value of y,
            y = 3;                           //   to make sure it's in
        if (y > canvas.getHeight() - 4)       //   the drawing area.
            y = canvas.getHeight() - 4;

        g.strokeLine(prevX, prevY, x, y);  // Draw the line.

        prevX = x;  // Get ready for the next line segment in the curve.
        prevY = y;*/

    } // end mouseDragged()

}
