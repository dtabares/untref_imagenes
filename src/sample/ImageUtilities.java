package sample;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;


public class ImageUtilities {

    private String supportedFormats[] = { "raw", "ppm", "pgm", "bmp", "png" };
    private String currentImageFormat;

    public String getCurrentImageFormat() {
        return currentImageFormat;
    }



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

    public BufferedImage readPGMNew(File file) throws IOException {
        //Magic number representing the binary PGM file type.
        final String MAGIC = "P5";

        //The maximum gray value.
        final int MAXVAL = 255;
        BufferedImage bimg = null;
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));

        try {
            if (!next(stream).equals(MAGIC))
                throw new IOException("File " + file + " is not a binary PGM image.");
            final int col = Integer.parseInt(next(stream));
            final int row = Integer.parseInt(next(stream));
            final int max = Integer.parseInt(next(stream));
            bimg = new BufferedImage(col, row, BufferedImage.TYPE_BYTE_GRAY);
            if (max < 0 || max > MAXVAL)
                throw new IOException("The image's maximum gray value must be in range [0, " + MAXVAL + "].");
            for (int i = 0; i < row; ++i) {
                for (int j = 0; j < col; ++j) {
                    final int p = stream.read();
                    if (p == -1)
                        throw new IOException("Reached end-of-file prematurely.");
                    else if (p < 0 || p > max)
                        throw new IOException("Pixel value " + p + " outside of range [0, " + max + "].");
                    Color c =  new Color(p,p,p);

                    bimg.setRGB(j, i, c.getRGB());
                }
            }
            return bimg;
        }
        finally {
            stream.close();
        }
    }

    private static String next(final InputStream stream) throws IOException {
        //Character indicating a comment.
        final char COMMENT = '#';
        final List<Byte> bytes = new ArrayList<Byte>();
        while (true) {
            final int b = stream.read();

            if (b != -1) {

                final char c = (char) b;
                if (c == COMMENT) {
                    int d;
                    do {
                        d = stream.read();
                    } while (d != -1 && d != '\n' && d != '\r');
                } else if (!Character.isWhitespace(c)) {
                    bytes.add((byte) b);
                } else if (bytes.size() > 0) {
                    break;
                }

            } else {
                break;
            }

        }
        final byte[] bytesArray = new byte[bytes.size()];
        for (int i = 0; i < bytesArray.length; ++i)
            bytesArray[i] = bytes.get(i);
        return new String(bytesArray);
    }

    public String getImageExtension(String filename)
    {
        String[] splittedName = filename.split("\\.");
        this.currentImageFormat = splittedName[splittedName.length -1].toLowerCase();
        return this.currentImageFormat;
    }

    public String getPixelInformation(BufferedImage image, int x, int y)
    {
        String outPutMessage = "Pixel Information: ";
        int rgb = image.getRGB(x,y);
        System.out.println("Pixel RGB:" + image.getRGB(x,y));
        System.out.println("Color Model:" + image.getColorModel());
        System.out.println("Image Type:" + image.getType());
        Color c =  new Color( image.getRGB(x,y));
        System.out.println("Image R:" + c.getRed());
        System.out.println("Image G:" + c.getGreen());
        System.out.println("Image B:" + c.getBlue());
        outPutMessage =  outPutMessage + "Red: " + c.getRed() + " Green: " + c.getGreen() + " Blue: " + c.getBlue();
        return outPutMessage;
    }

}
