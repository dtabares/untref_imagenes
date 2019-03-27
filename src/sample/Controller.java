package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import javax.swing.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;

public class Controller extends BorderPane {

    private File imageFile = null;
    private BufferedImage leftImage = null;
    private BufferedImage rightImage = null;
    private String supportedFormats[] = { ".raw", ".ppm", ".pgm", ".jpg", ".png" } ;

    @FXML private AnchorPane leftPane;
    @FXML private AnchorPane rightPane;

    public void initialize()throws IOException{
        System.out.println("Starting...");
    }
    
    public boolean isSupportedFormat(File f )
    {
        for (String s : supportedFormats)
        {
            if (f.getName().toLowerCase().contains(s))
            {
                return true;
            }
        }
        return false;
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
        try
        {
            fc.setTitle("Select Directory");
            File f = fc.showOpenDialog(browser);
            if(isSupportedFormat(f))
            {
                imageFile = new File(f.getAbsolutePath());
                if(isRawFormat(imageFile))
                {
                    int width = Integer.valueOf(JOptionPane.showInputDialog(
                            null, "Width", "Insert Width",
                            JOptionPane.DEFAULT_OPTION));

                    int height = Integer.valueOf(JOptionPane.showInputDialog(
                            null, "Height", "Insert Height",
                            JOptionPane.DEFAULT_OPTION));
                    BufferedImage bimg = openRawImage(f,width,height);
                    wimg = readRawImage(bimg,width,height);
                }
                else
                {
                    BufferedImage bimg = ImageIO.read(f);
                    wimg = readImage(bimg);
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

    private BufferedImage openRawImage(File originalFile, int width,
                                       int height) {

        BufferedImage image = null;
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(originalFile.toPath());

            image = new BufferedImage(width, height,
                    BufferedImage.TYPE_3BYTE_BGR);
            int counter = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {

                    int alpha = -16777216;
                    int red = ((int) bytes[counter] & 0xff) << 16;
                    int green = ((int) bytes[counter] & 0xff) << 8;
                    int blue = ((int) bytes[counter] & 0xff);

                    int color = alpha + red + green + blue;

                    image.setRGB(j, i, color);

                    counter++;
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
        return image;
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
                WriteImage(leftImage, f , ext.substring(1) );
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

    public WritableImage readImage(BufferedImage bimg)
    {
        WritableImage wimg = null;
        try {
            if (bimg != null) {
                wimg = new WritableImage(bimg.getWidth(), bimg.getHeight());
                PixelWriter pw = wimg.getPixelWriter();
                for (int x = 0; x < bimg.getWidth(); x++) {
                    for (int y = 0; y < bimg.getHeight(); y++) {

                        pw.setArgb(x, y, bimg.getRGB(x, y));
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return wimg;
    }

    public WritableImage readRawImage(BufferedImage bimg, int width, int height)
    {
        WritableImage wimg = null;
        try {
            if (bimg != null) {
                wimg = new WritableImage(width, height);
                PixelWriter pw = wimg.getPixelWriter();
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++)
                    {
                        pw.setArgb(x, y, bimg.getRGB(x, y));
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return wimg;
    }


    public void WriteImage(BufferedImage image, File f, String format) throws IOException
    {
        try {
            ImageIO.write(image, format, f);
            System.out.println("Writing complete");
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void ShowAlert(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //@FXML public void createImageWithCircle(int width, int height, int rad)
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
                WriteImage(bImage, f , ext );
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
