package sample;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

public class ImageUtilities {

    private String supportedFormats[] = { "raw", "ppm", "pgm", "bmp", "png" } ;

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

        BufferedImage bimg = null;
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(originalFile.toPath());

            bimg = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            int counter = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {

                    int alpha = -16777216;
                    int red = ((int) bytes[counter] & 0xff) << 16;
                    int green = ((int) bytes[counter] & 0xff) << 8;
                    int blue = ((int) bytes[counter] & 0xff);

                    int color = alpha + red + green + blue;

                    bimg.setRGB(j, i, color);

                    counter++;
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
        return bimg;
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

    public BufferedImage readPGM(File file){
        int width = 0;
        int height = 0;
        BufferedImage bimg = null;
        //Read Header
        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String magicNumber = br.readLine(); // first line contains P2 or P5
            String line = br.readLine();
            while (line.startsWith("#")) { //ignore comments
                line = br.readLine();
            }
            Scanner s = new Scanner(line);
            width = s.nextInt();
            height = s.nextInt();
            line = br.readLine(); // third line contains maxVal
            s = new Scanner(line);
            int maxValue = s.nextInt();
            br.close();

            bimg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            //Read Body
            DataInputStream stream;
            stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            int newlinecount = 0;
            char previous = (char) 'a';
            char c;

            do {
                c = (char) stream.readByte();
                if (c == '\n' || c == '\r') {
                    newlinecount++;
                }
                if (c == (char) '#' && (previous == '\n' || previous == '\r')) {
                    newlinecount--;
                }
                previous = c;
            } while (newlinecount < 3);
            System.out.println("Skipped header. Start reading binary content...");

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    byte b = stream.readByte();
                    Byte b2 = b;
                    //pixels[row][col] = b;
                    bimg.setRGB(col, row, (int)(b2.intValue() * 255 / maxValue));
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return bimg;
    }

    public String getImageExtension(String filename)
    {
        String[] splittedName = filename.split("\\.");
        return splittedName[splittedName.length -1].toLowerCase();
    }

}
