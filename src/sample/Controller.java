package sample;

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

    @FXML private AnchorPane PanelCentral;


    public void initialize()throws IOException{
        System.out.println("Inicializando...");
        PanelCentral.setStyle("-fx-background-color: #605d6d;");
    }

    @FXML public void openFile() {
        Stage directorioStage = null;
        FileChooser fc = new FileChooser();
        try {
            fc.setTitle("Seleccionar Directorio");
            if (imageFile!=null){
                fc.setInitialDirectory(imageFile);
            }
            imageFile = fc.showOpenDialog(directorioStage);
            if(imageFile.getName().toLowerCase().contains(".raw") ||
                    imageFile.getName().toLowerCase().contains(".ppm") ||
                        imageFile.getName().toLowerCase().contains(".pgm") ||
                            imageFile.getName().toLowerCase().contains(".jpg") ||
                                imageFile.getName().toLowerCase().contains(".png")
            ) {
                imageFile = new File(imageFile.getAbsolutePath());
                System.out.println(imageFile.getAbsolutePath());
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("La extension del archivo no esta soportada");
                alert.showAndWait();
            }
            if(imageFile != null){
                BufferedImage bimg = ReadImage(imageFile);
                WritableImage wimg = null;
                if (bimg != null) {
                    wimg = new WritableImage(bimg.getWidth(), bimg.getHeight());
                    PixelWriter pw = wimg.getPixelWriter();
                    for (int x = 0; x < bimg.getWidth(); x++) {
                        for (int y = 0; y < bimg.getHeight(); y++) {
                            pw.setArgb(x, y, bimg.getRGB(x, y));
                        }
                    }
                }
                ImageView Imagen = new ImageView(wimg);
                PanelCentral.getChildren().setAll(Imagen);
            }
        }
        catch (Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            imageFile = null;
        }
        fc.setInitialDirectory(null);
        directorioStage = null;
    }
    @FXML public void closeApplication(){
        System.exit(0);
    }

    public BufferedImage ReadImage(File f)throws IOException
    {
        int width = 600;
        int height = 800;
        BufferedImage image = null;
        try {
            image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
            image = ImageIO.read(f);
            System.out.println("Reading complete");
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return image;
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
}
