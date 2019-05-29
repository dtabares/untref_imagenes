package sample;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ImageSequence {

    public List<BufferedImage> imageList;

    public ImageSequence()throws Exception{
        Stage browser = new Stage();
        DirectoryChooser dc = new DirectoryChooser();
        File directory = dc.showDialog(browser);
        dc.setTitle("Select Directory");
        imageList = new LinkedList<>();
        for (File f: directory.listFiles()){
            imageList.add(ImageIO.read(new File(f.getAbsolutePath())));
        }
    }
}
