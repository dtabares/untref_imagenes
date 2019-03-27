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
import java.nio.file.Files;
import javax.imageio.ImageIO;
import javax.swing.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;

public class Controller extends BorderPane {

    private File archivoImagen = null;
    private BufferedImage imagenIzquierda = null;
    private BufferedImage imagenDerecha = null;
    private String formatos [] = { ".raw", ".ppm", ".pgm", ".jpg", ".png" } ;

    @FXML private AnchorPane PanelIzq;
    @FXML private AnchorPane PanelDer;

    public void initialize()throws IOException{
        System.out.println("Inicializando...");
    }
    
    public boolean formatoSoportado(File f )
    {
        for (String s : formatos )
        {
            if (f.getName().toLowerCase().contains(s))
            {
                return true;
            }
        }
        return false;
    }
    public boolean esFormatoRaw(File f)
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
            fc.setTitle("Seleccionar Directorio");
            File f = fc.showOpenDialog(browser);
            if(formatoSoportado(f))
            {
                archivoImagen = new File(f.getAbsolutePath());
                if(esFormatoRaw(archivoImagen))
                {
                    int ancho = Integer.valueOf(JOptionPane.showInputDialog(
                            null, "Ancho", "Ingresar Dimensiones",
                            JOptionPane.DEFAULT_OPTION));

                    int alto = Integer.valueOf(JOptionPane.showInputDialog(
                            null, "Alto", "Ingresar Dimensiones",
                            JOptionPane.DEFAULT_OPTION));
                    BufferedImage bimg = abrirImagenRaw(f,ancho,alto);
                    wimg = leerImagenRaw(bimg,ancho,alto);
                }
                else
                {
                    BufferedImage bimg = ImageIO.read(f);
                    wimg = leerImagen(bimg);
                }
                mostrarImagenEnPanel(wimg,PanelIzq);
                mostrarImagenEnPanel(wimg,PanelDer);
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
            archivoImagen = null;
        }
    }

    private BufferedImage abrirImagenRaw(File archivoActual, int width,
                                         int height) {

        BufferedImage imagen = null;
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(archivoActual.toPath());

            imagen = new BufferedImage(width, height,
                    BufferedImage.TYPE_3BYTE_BGR);
            int contador = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {

                    int alpha = -16777216;
                    int red = ((int) bytes[contador] & 0xff) << 16;
                    int green = ((int) bytes[contador] & 0xff) << 8;
                    int blue = ((int) bytes[contador] & 0xff);

                    int color = alpha + red + green + blue;

                    imagen.setRGB(j, i, color);

                    contador++;
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
        return imagen;
    }

    @FXML public void saveImageFile()
    {
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        if (imagenIzquierda != null)
        {
            try {
                fc.setTitle("Seleccionar Archivo");
                File f = fc.showSaveDialog (browser);
                String ext = f.getName().substring(f.getName().lastIndexOf("."));
                WriteImage(imagenIzquierda, f , ext.substring(1) );
            }
            catch (Exception e)
            {
                ShowAlert(e.getMessage());
            }
            fc.setInitialDirectory(null);
        }
        else
            {
                ShowAlert("No hay imagen para guardar");
            }
    }

    @FXML public void closeApplication(){
        System.exit(0);
    }

    public void mostrarImagenEnPanel(WritableImage wimg, AnchorPane panel)
    {
        ImageView Imagen = new ImageView(wimg);
        panel.getChildren().setAll(Imagen);
    }

    public WritableImage leerImagen(BufferedImage bimg)
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

    public WritableImage leerImagenRaw(BufferedImage bimg, int ancho, int alto)
    {
        WritableImage wimg = null;
        try {
            if (bimg != null) {
                wimg = new WritableImage(ancho, alto);
                PixelWriter pw = wimg.getPixelWriter();
                for (int x = 0; x < ancho; x++) {
                    for (int y = 0; y < alto; y++)
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

    public void crearImagenConCirculo(int ancho, int alto, int rad)
    {
        //Crea un shape circulo blanco centrado de radio definido
        Circle c = new Circle();
        c.setCenterX(ancho/2);
        c.setCenterY(alto/2);
        c.setRadius(rad);
        c.setFill(Color.WHITE);
        PanelIzq.getChildren().setAll(c);
        WritableImage image = PanelIzq.snapshot(null, null);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        try {
                fc.setTitle("Seleccionar Archivo");
                File f = fc.showSaveDialog (browser);
                String ext = f.getName().substring(f.getName().lastIndexOf("."));
                WriteImage(bImage, f , ext.substring(1) );
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
