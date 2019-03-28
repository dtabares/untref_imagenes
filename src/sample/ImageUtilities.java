package sample;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class ImageUtilities {

    private String supportedFormats[] = { "raw", "ppm", "pgm", "jpg", "png" } ;

    public boolean isSupportedFormat(String extension )
    {
        for (String s : supportedFormats)
        {
            if (extension.toLowerCase().equals(s))
            {
                return true;
            }
        }
        return false;
    }

    public BufferedImage openRawImage(File originalFile, int width,
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

    public void readPGM(String filename){
        // image buffer for plain gray-scale pixel values
        int[][] pixels;
        try {
            Scanner infile = new Scanner(new FileReader(filename));
            // process the top 4 header lines
            String filetype=infile.nextLine();
            if (!filetype.equalsIgnoreCase("p2")) {
                System.out.println("[readPGM]Cannot load the image type of "+filetype);
                return;
            }
            infile.nextLine();
            int cols = infile.nextInt();
            int rows = infile.nextInt();
            int maxValue = infile.nextInt();
            pixels = new int[rows][cols];
            System.out.println("Reading in image from " + filename + " of size " + rows + " by " + cols);
            // process the rest lines that hold the actual pixel values
            for (int r=0; r<rows; r++)
                for (int c=0; c<cols; c++)
                    pixels[r][c] = (int)(infile.nextInt()*255.0/maxValue);
            infile.close();
        } catch(FileNotFoundException fe) {
            System.out.println("Had a problem opening a file.");
        } catch (Exception e) {
            System.out.println(e.toString() + " caught in readPPM.");
            e.printStackTrace();
        }
    }

    public String getImageExtension(String filename)
    {
        String[] splittedName = filename.split("\\.");
        return splittedName[splittedName.length -1].toLowerCase();
    }

}
