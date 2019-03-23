package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;

public class Controller extends BorderPane {

    private File imageFile = null;
    private BufferedImage modifiedImage = null;

    @FXML private AnchorPane PanelIzq;
    @FXML private AnchorPane PanelDer;

    public void initialize()throws IOException{
        System.out.println("Inicializando...");
//        PanelIzq.setStyle("-fx-background-color: #605d6d; -fx-border-color: #000000; -fx-border-width: 1;");
//        PanelDer.setStyle("-fx-background-color: #605d6d; -fx-border-color: #000000; -fx-border-width: 1;");
    }

    @FXML public void openImageFile() {
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        try {
            fc.setTitle("Seleccionar Directorio");
            if (imageFile!=null){
                fc.setInitialDirectory(imageFile);
            }
            imageFile = fc.showOpenDialog(browser);
            if(imageFile.getName().toLowerCase().contains(".raw") ||
                    // ver http://lclevy.free.fr/cr2/#intro
                    imageFile.getName().toLowerCase().contains(".ppm") ||
                        imageFile.getName().toLowerCase().contains(".pgm") ||
                            imageFile.getName().toLowerCase().contains(".jpg") ||
                                imageFile.getName().toLowerCase().contains(".png")
            ) {
                imageFile = new File(imageFile.getAbsolutePath());
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("La extension del archivo no esta soportada");
                alert.showAndWait();
            }

                WritableImage wimg = ReadImage(imageFile);
                ImageView Imagen = new ImageView(wimg);
                PanelIzq.getChildren().setAll(Imagen);
                //Prueba para salvar imagen
                modifiedImage = SwingFXUtils.fromFXImage(wimg, modifiedImage);
                PanelDer.getChildren().setAll(new ImageView(wimg));
        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            imageFile = null;
        }
        fc.setInitialDirectory(null);
    }

    @FXML public void saveImageFile(){
        Stage browser = new Stage();
        FileChooser fc = new FileChooser();
        if (modifiedImage != null)
        {
            try {
                fc.setTitle("Seleccionar Archivo");
                File f = fc.showSaveDialog (browser);
                String ext = f.getName().substring(f.getName().lastIndexOf("."));
                WriteImage(modifiedImage, f , ext.substring(1) );
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

    public WritableImage ReadImage(File f)throws IOException
    {
        WritableImage wimg = null;
        try {
            BufferedImage bimg = ImageIO.read(f);
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
}
