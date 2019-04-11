package sample;

import com.sun.istack.internal.Nullable;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;
import static sample.ColorUtilities.createRGB;
import static sample.InterfaceHelper.getInputDialog;


public class ImageUtilities {

    private String supportedFormats[] = {"raw", "ppm", "pgm", "bmp", "png", "jpg"};
    private String currentImageFormat;

    public String getCurrentImageFormat() {
        return currentImageFormat;
    }

    public boolean isSupportedFormat(String extension) {
        for (String s : supportedFormats) {
            if (extension.toLowerCase().equals(s)) {
                return true;
            }
        }
        return false;
    }

    public BufferedImage openRawImage(File f, int width, int height) {

        BufferedImage bimg = null;
        byte[] rawImageContent;
        try {
            rawImageContent = Files.readAllBytes(f.toPath());

            bimg = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            int counter = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {

                    bimg.setRGB(j, i, createRGB(rawImageContent[counter]));

                    counter++;
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
        return bimg;
    }

    public WritableImage readImage(BufferedImage bimg) {
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
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return wimg;
    }

    public WritableImage readRawImage(BufferedImage bimg, int width, int height) {
        WritableImage wimg = null;
        try {
            if (bimg != null) {
                wimg = new WritableImage(width, height);
                PixelWriter pw = wimg.getPixelWriter();
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        pw.setArgb(x, y, bimg.getRGB(x, y));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return wimg;
    }

    public void WriteImage(BufferedImage image, File f) {
        String format = this.getImageExtension(f.getName());

        try {
            switch (format) {
                case "raw":
                    int width = Integer.valueOf(getInputDialog("Open Image", "Raw Image Information", "Insert Image Width"));
                    int height = Integer.valueOf(getInputDialog("Open Image", "Raw Image Information", "Insert Image Height"));
                    this.saveRawImage(image, f, height, width);
                    break;
                case "ppm":
                    //Do Something
                    break;
                case "pgm":
                    this.savePgmImage(image, f);
                    break;
                default:
                    ImageIO.write(image, format, f);
                    System.out.println("Writing complete");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void saveRawImage(BufferedImage image, File f, int height, int width) {
        byte[] rawContent = new byte[height * width];
        int counter = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //System.out.println(ColorUtilities.byteFromRGB(image.getRGB(j,i)));
                rawContent[counter] = ColorUtilities.byteFromRGB(image.getRGB(j, i));
                counter++;
            }
        }

        try {
            OutputStream os = new FileOutputStream(f);
            os.write(rawContent);
            os.close();

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    public BufferedImage readPGM(File file) throws IOException {
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
            bimg = new BufferedImage(col, row, TYPE_BYTE_GRAY);
            if (max < 0 || max > MAXVAL)
                throw new IOException("The image's maximum gray value must be in range [0, " + MAXVAL + "].");
            for (int i = 0; i < row; ++i) {
                for (int j = 0; j < col; ++j) {
                    final int p = stream.read();
                    if (p == -1)
                        throw new IOException("Reached end-of-file prematurely.");
                    else if (p < 0 || p > max)
                        throw new IOException("Pixel value " + p + " outside of range [0, " + max + "].");
                    System.out.println(p);
                    int rgb = createRGB(p, p, p);

                    bimg.setRGB(j, i, rgb);
                }
            }
            return bimg;
        } finally {
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

    public String getImageExtension(String filename) {
        String[] splittedName = filename.split("\\.");
        this.currentImageFormat = splittedName[splittedName.length - 1].toLowerCase();
        return this.currentImageFormat;
    }

    public String getPixelInformation(BufferedImage image, int x, int y) {
        String outPutMessage = "Pixel Information: ";
        final int rgb = image.getRGB(x, y);
        System.out.println("Pixel RGB:" + image.getRGB(x, y));
        System.out.println("Color Model:" + image.getColorModel());
        System.out.println("Image Type:" + image.getType());
        final int red = ColorUtilities.getRed(rgb);
        final int green = ColorUtilities.getGreen(rgb);
        final int blue = ColorUtilities.getBlue(rgb);
        System.out.println("Image R:" + red);
        System.out.println("Image G:" + green);
        System.out.println("Image B:" + blue);
        outPutMessage = outPutMessage + "Red: " + red + " Green: " + green + " Blue: " + blue;
        return outPutMessage;
    }

    public BufferedImage modifyPixelInformation(BufferedImage image, int x, int y, int red, int green, int blue) {
        int newRGB = ColorUtilities.createRGB(red, green, blue);
        image.setRGB(x, y, newRGB);
        return image;
    }

    public BufferedImage imageAddition(BufferedImage bimg1, BufferedImage bimg2) {
        BufferedImage temp = null;
        try {
            if (bimg1.getType() == bimg2.getType()) {
                boolean firstIsWidest = bimg1.getWidth() >= bimg2.getWidth();
                boolean firstIsHigher = bimg1.getHeight() >= bimg2.getHeight();
                int maxWidth;
                int minWidth;
                int maxHeight;
                int minHeight;
                if (bimg1.getWidth() >= bimg2.getWidth()) {
                    maxWidth = bimg1.getWidth();
                    minWidth = bimg2.getWidth();
                } else {
                    maxWidth = bimg2.getWidth();
                    minWidth = bimg1.getWidth();
                }
                if (bimg1.getHeight() >= bimg2.getHeight()) {
                    maxHeight = bimg1.getHeight();
                    minHeight = bimg2.getHeight();
                } else {
                    maxHeight = bimg2.getHeight();
                    minHeight = bimg1.getHeight();
                }
                temp = new BufferedImage(maxWidth, maxHeight, bimg1.getType());

                int [][] redMatrix = new int [maxWidth][maxHeight];
                int [][] greenMatrix = new int [maxWidth][maxHeight];
                int [][] blueMatrix = new int [maxWidth][maxHeight];

                int r,b,g;
                int max = 0;
                for (int i = 0; i < maxWidth; i++) {
                    for (int j = 0; j < maxHeight; j++) {
                        if (i < minWidth && j < minHeight) {
                            r = ColorUtilities.getRed(bimg1.getRGB(i, j)) + ColorUtilities.getRed(bimg2.getRGB(i, j));
                            g = ColorUtilities.getGreen(bimg1.getRGB(i, j)) + ColorUtilities.getGreen(bimg2.getRGB(i, j));
                            b = ColorUtilities.getBlue(bimg1.getRGB(i, j)) + ColorUtilities.getBlue(bimg2.getRGB(i, j));
                            redMatrix[i][j]=r;
                            greenMatrix[i][j]=g;
                            blueMatrix[i][j]=b;
                            if( r > max ){
                                max = r;
                            }
                            if( g > max ){
                                max = g;
                            }
                            if( b > max ){
                                max = b;
                            }
                        }
                        //A
                        else if (i < minWidth && j >= minHeight) {
                            if (firstIsHigher) {
                                redMatrix[i][j] = ColorUtilities.getRed(bimg1.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg1.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg1.getRGB(i, j));
                            } else {
                                redMatrix[i][j] = ColorUtilities.getRed(bimg2.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg2.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg2.getRGB(i, j));
                            }
                        }
                        //B
                        else if (i >= minWidth && j < minHeight) {
                            if (firstIsWidest) {
                                redMatrix[i][j] = ColorUtilities.getRed(bimg1.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg1.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg1.getRGB(i, j));
                            } else {
                                redMatrix[i][j] = ColorUtilities.getRed(bimg2.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg2.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg2.getRGB(i, j));
                            }
                        }
                        else {
                            if (firstIsWidest && firstIsHigher){
                                redMatrix[i][j] = ColorUtilities.getRed(bimg1.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg1.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg1.getRGB(i, j));
                            }
                            else if (!firstIsWidest && !firstIsHigher){
                                redMatrix[i][j] = ColorUtilities.getRed(bimg2.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg2.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg2.getRGB(i, j));
                            }
                            else{
                                redMatrix[i][j] = 0;
                                greenMatrix[i][j] = 0;
                                blueMatrix[i][j] = 0;
                            }

                        }
                    }

                }
                int newR,newG,newB;
                for (int i = 0; i < temp.getWidth(); i++){
                    for (int j = 0; j < temp.getHeight(); j++){
                        if (max > 255){
                            newR = ((redMatrix[i][j] * 255) / max);
                            newG = ((greenMatrix[i][j] * 255) / max);
                            newB = ((blueMatrix[i][j] * 255) / max);
                            temp.setRGB(i,j,ColorUtilities.createRGB(newR,newG,newB));
                        }
                        else{
                            temp.setRGB(i,j,ColorUtilities.createRGB(redMatrix[i][j], greenMatrix[i][j], blueMatrix[i][j]));
                        }

                    }
                }
            } else {
                Alerts.showAlert("No se pueden sumar formatos diferentes");
            }
        } catch (Exception e) {
            Alerts.showAlert(e.getMessage());
        }

        return temp;
    }

    public BufferedImage imageDifference(BufferedImage bimg1, BufferedImage bimg2) {
        BufferedImage temp = null;
        try {
            if (bimg1.getType() == bimg2.getType()) {
                boolean firstIsWidest = bimg1.getWidth() >= bimg2.getWidth();
                boolean firstIsHigher = bimg1.getHeight() >= bimg2.getHeight();
                int maxWidth;
                int minWidth;
                int maxHeight;
                int minHeight;
                if (bimg1.getWidth() >= bimg2.getWidth()) {
                    maxWidth = bimg1.getWidth();
                    minWidth = bimg2.getWidth();
                } else {
                    maxWidth = bimg2.getWidth();
                    minWidth = bimg1.getWidth();
                }
                if (bimg1.getHeight() >= bimg2.getHeight()) {
                    maxHeight = bimg1.getHeight();
                    minHeight = bimg2.getHeight();
                } else {
                    maxHeight = bimg2.getHeight();
                    minHeight = bimg1.getHeight();
                }
                temp = new BufferedImage(maxWidth, maxHeight, bimg1.getType());

                int [][] redMatrix = new int [maxWidth][maxHeight];
                int [][] greenMatrix = new int [maxWidth][maxHeight];
                int [][] blueMatrix = new int [maxWidth][maxHeight];

                int r,b,g;
                int max = 0, min = 255;
                for (int i = 0; i < maxWidth; i++) {
                    for (int j = 0; j < maxHeight; j++) {
                        if (i < minWidth && j < minHeight) {
                            r = ColorUtilities.getRed(bimg1.getRGB(i, j)) - ColorUtilities.getRed(bimg2.getRGB(i, j));
                            g = ColorUtilities.getGreen(bimg1.getRGB(i, j)) - ColorUtilities.getGreen(bimg2.getRGB(i, j));
                            b = ColorUtilities.getBlue(bimg1.getRGB(i, j)) - ColorUtilities.getBlue(bimg2.getRGB(i, j));
                            redMatrix[i][j]=r;
                            greenMatrix[i][j]=g;
                            blueMatrix[i][j]=b;
                            if( r > max ){
                                max = r;
                            }
                            if( g > max ){
                                max = g;
                            }
                            if( b > max ){
                                max = b;
                            }
                            if( r < min){
                                min = r;
                            }
                            if( g < min ){
                                min = g;
                            }
                            if( b < min ){
                                min = b;
                            }
                        }
                        //A
                        else if (i < minWidth && j >= minHeight) {
                            if (firstIsHigher) {
                                redMatrix[i][j] = ColorUtilities.getRed(bimg1.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg1.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg1.getRGB(i, j));
                            } else {
                                redMatrix[i][j] = ColorUtilities.getRed(bimg2.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg2.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg2.getRGB(i, j));
                            }
                        }
                        //B
                        else if (i >= minWidth && j < minHeight) {
                            if (firstIsWidest) {
                                redMatrix[i][j] = ColorUtilities.getRed(bimg1.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg1.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg1.getRGB(i, j));
                            } else {
                                redMatrix[i][j] = ColorUtilities.getRed(bimg2.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg2.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg2.getRGB(i, j));
                            }
                        }
                        else {
                            if (firstIsWidest && firstIsHigher){
                                redMatrix[i][j] = ColorUtilities.getRed(bimg1.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg1.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg1.getRGB(i, j));
                            }
                            else if (!firstIsWidest && !firstIsHigher){
                                redMatrix[i][j] = ColorUtilities.getRed(bimg2.getRGB(i, j));
                                greenMatrix[i][j] = ColorUtilities.getGreen(bimg2.getRGB(i, j));
                                blueMatrix[i][j] = ColorUtilities.getBlue(bimg2.getRGB(i, j));
                            }
                            else{
                                redMatrix[i][j] = 0;
                                greenMatrix[i][j] = 0;
                                blueMatrix[i][j] = 0;
                            }

                        }
                    }

                }
                int newR,newG,newB;
                for (int i = 0; i < temp.getWidth(); i++){
                    for (int j = 0; j < temp.getHeight(); j++){
                        if (min < 0){
                            if (max !=0) {
                                newR = (((redMatrix[i][j] + 255) * 255) / Math.abs(max));
                                newG = (((greenMatrix[i][j] + 255) * 255) / Math.abs(max));
                                newB = (((blueMatrix[i][j] + 255) * 255) / Math.abs(max));
                                temp.setRGB(i, j, ColorUtilities.createRGB(newR, newG, newB));
                            }
                            else{
                                newR = (redMatrix[i][j] + 255);
                                newG = (greenMatrix[i][j] + 255);
                                newB = (blueMatrix[i][j] + 255);
                                temp.setRGB(i, j, ColorUtilities.createRGB(newR, newG, newB));
                            }
                        }
                        else{
                            temp.setRGB(i,j,ColorUtilities.createRGB(redMatrix[i][j], greenMatrix[i][j], blueMatrix[i][j]));
                        }

                    }
                }
            } else {
                Alerts.showAlert("No se pueden sumar formatos diferentes");
            }
        } catch (Exception e) {
            Alerts.showAlert(e.getMessage());
        }

        return temp;
    }

    public BufferedImage imageScalarProduct(BufferedImage bimg, int scalar) {
        BufferedImage temp = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
        boolean adjustDynamicRangeRequired = false;
        Channel redChannel = new Channel(bimg.getWidth(), bimg.getHeight());
        Channel greenChannel = new Channel(bimg.getWidth(), bimg.getHeight());
        Channel blueChannel = new Channel(bimg.getWidth(), bimg.getHeight());
        Channel[] adjustedChannels;
        int rgb;

        try {
            temp = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());

            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    int newRed = ColorUtilities.getRed(bimg.getRGB(i,j)) * scalar;
                    int newGreen = ColorUtilities.getGreen(bimg.getRGB(i,j)) * scalar;
                    int newBlue = ColorUtilities.getBlue(bimg.getRGB(i,j)) * scalar;
                    redChannel.setValue(i,j,newRed) ;
                    greenChannel.setValue(i,j,newGreen);
                    blueChannel.setValue(i,j,newBlue);

                    if(newRed > 255 || newGreen > 255 || newBlue > 255)
                    {
                        adjustDynamicRangeRequired = true;
                    }

                }
            }

            if (adjustDynamicRangeRequired)
            {
                adjustedChannels = this.adjustDynamicRange(redChannel,greenChannel,blueChannel);
                redChannel = adjustedChannels[0];
                greenChannel = adjustedChannels[1];
                blueChannel = adjustedChannels[2];
            }


            // recorro las matrices y armo la buffered image
            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    rgb = ColorUtilities.createRGB(redChannel.getValue(i,j), greenChannel.getValue(i,j),blueChannel.getValue(i,j));
                    temp.setRGB(i,j,rgb);
                }
            }
        } catch (Exception e) {
            Alerts.showAlert(e.getMessage());
        }
        return (temp);
    }

    //reemplaza c por un escalar
    public BufferedImage dynamicRangeCompression(BufferedImage bimg, int multiplier) {
        BufferedImage temp = null;
        try {
            temp = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
            int p, red, green, blue;
            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    p = (bimg.getRGB(i, j));
                    red = ColorUtilities.getRed(p);
                    green = ColorUtilities.getGreen(p);
                    blue = ColorUtilities.getBlue(p);
                    red = (int)Math.round((100 / multiplier) * Math.log10(1 + red));
                    green = (int) Math.round((100 / multiplier) * Math.log10(1 + green));
                    blue = (int) Math.round((100 / multiplier) * Math.log10(1 + blue));
                    int rgb = ColorUtilities.createRGB(red, green, blue);
                    temp.setRGB(i, j, rgb);
                }
            }
        } catch (Exception e) {
            Alerts.showAlert(e.getMessage());
        }
        return temp;
    }

    //usa la formula entera
    public Channel[] adjustDynamicRange(Channel redChannel, Channel greenChannel, Channel blueChannel) {
        Channel[] channels = new Channel[3];
        try {
            
            int rmax = redChannel.getMaxValue();
            int gmax = greenChannel.getMaxValue();
            int bmax = blueChannel.getMaxValue();
            int red,green,blue;

            Channel adjustedRedChannel, adjustedGreenChannel,  adjustedBlueChannel;
            adjustedRedChannel = new Channel(redChannel.getWidth(),redChannel.getHeight());
            adjustedGreenChannel = new Channel(greenChannel.getWidth(),greenChannel.getHeight());
            adjustedBlueChannel = new Channel(blueChannel.getWidth(),blueChannel.getHeight());

            double cr = 255 / Math.log10(1 + rmax);
            double cg = 255 / Math.log10(1 + gmax);
            double cb = 255 / Math.log10(1 + bmax);

            for (int i = 0; i < redChannel.getWidth(); i++) {
                for (int j = 0; j < redChannel.getHeight(); j++) {
                    red = (int) Math.round(cr * Math.log10((1 + redChannel.getValue(i,j))));
                    green = (int) Math.round(cg * Math.log10((1 + greenChannel.getValue(i,j))));
                    blue = (int) Math.round(cb * Math.log10((1 + blueChannel.getValue(i,j))));

                    adjustedRedChannel.setValue(i,j,red);
                    adjustedGreenChannel.setValue(i,j,green);
                    adjustedBlueChannel.setValue(i,j,blue);

                }
            }

            channels[0] = adjustedRedChannel;
            channels[1] = adjustedGreenChannel;
            channels[2] = adjustedBlueChannel;

        } catch (Exception e) {
            Alerts.showAlert(e.getMessage());
        }


        return channels;
    }

    public BufferedImage gammaPowFunction(BufferedImage bimg, double gamma) {
        BufferedImage temp = null;
        try {
            temp = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());

            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    int p = (bimg.getRGB(i, j));
                    int red = ColorUtilities.getRed(p);
                    int green = ColorUtilities.getGreen(p);
                    int blue = ColorUtilities.getBlue(p);
                    red = (int) (Math.pow((double) 255, (1.0 - gamma)) * Math.pow(red,gamma));
                    green = (int) (Math.pow((double) 255, (1.0 - gamma)) * Math.pow(green,gamma));
                    blue = (int) (Math.pow((double) 255, (1.0 - gamma)) * Math.pow(blue,gamma));
                    int rgb = ColorUtilities.createRGB(red,green,blue);
                    temp.setRGB(i, j, rgb);
                }
            }
        } catch (Exception e) {
            Alerts.showAlert(e.getMessage());
        }
        //return adjustDynamicRange(temp);
        return (temp);
    }

    public BufferedImage imageNegative(BufferedImage bimg) {
        BufferedImage temp = null;
        try {
            temp = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());

            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    int p = (bimg.getRGB(i, j));
                    int red = 255 - ColorUtilities.getRed(p);
                    int green = 255 -  ColorUtilities.getGreen(p);
                    int blue = 255 -  ColorUtilities.getBlue(p);
                    int rgb = ColorUtilities.createRGB(red,green,blue);
                    temp.setRGB(i, j, rgb);
                }
            }
        } catch (Exception e) {
            Alerts.showAlert(e.getMessage());
        }
        return temp;
    }

    public int[][] getChannelMatrix(BufferedImage bimg){
        int rgb;
        int length = bimg.getWidth()*bimg.getHeight();
        int matrix[][] = new int[3][length];
        int counter = 0;
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                rgb = bimg.getRGB(i, j);
                matrix[0][counter] = ColorUtilities.getRed(rgb);
                matrix[1][counter] = ColorUtilities.getGreen(rgb);
                matrix[2][counter] = ColorUtilities.getBlue(rgb);
                counter++;
            }
        }
        return matrix;
    }

    public void printChannelMatrix(int [][] matrix){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Fernando.Ares\\Desktop\\Imagenes\\matrix.txt"));
            for (int i = 0; i < 1; i++){
                for (int j = 0; j < matrix[0].length; j++){
                    writer.write(matrix[i][j] + ",");
                }
                writer.write("\n");
            }
            writer.close();
        }
        catch(Exception e) {

        }

    }

    public void printChannelVector(int [] array){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Fernando.Ares\\Desktop\\Imagenes\\matrix.txt"));
            System.out.println("Printing vector...");
            for (int i = 0; i < array.length; i++){
                writer.write(array[i] + ",");
                System.out.print(array[i] + ",");
            }
            System.out.println("");
            writer.close();
        }
        catch(Exception e) {

        }

    }

    public int getChannelMax(int channel[]) {
        int max = -1;
        for (int i = 0; i < channel.length; i++) {
            int temp = channel[i];
            if (temp > max) {
                max = temp;
            }
        }
        return max;
    }

    public int getChannelMin(int channel[]) {
        int min = 256;
        for (int i = 0; i < channel.length; i++) {
            int temp = (int) channel[i];
            if (temp < min) {
                min = temp;
            }
        }
        return min;
    }

    public void savePgmImage(BufferedImage image, File f) throws IOException {
        final String MAGIC = "P5";
        final int MAXVAL = 255;
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));

        try {
            stream.write(MAGIC.getBytes());
            stream.write("\n".getBytes());
            stream.write(Integer.toString(image.getHeight()).getBytes());
            stream.write(" ".getBytes());
            stream.write(Integer.toString(image.getWidth()).getBytes());
            stream.write("\n".getBytes());
            stream.write(Integer.toString(MAXVAL).getBytes());
            stream.write("\n".getBytes());
            for (int i = 0; i < image.getHeight(); ++i) {
                for (int j = 0; j < image.getWidth(); ++j) {
                    final int p = image.getRGB(j, i);
                    final int red = ColorUtilities.getRed(p);
                    final int green = ColorUtilities.getGreen(p);
                    final int blue = ColorUtilities.getBlue(p);

                    System.out.println(red + ";" + green + ";" + blue);
                    if (red != green || red != blue || green != blue)
                        throw new IOException("R G B should be equal to be grey! Red: " + red + " Green: " + green + " Blue: " + blue);
                    if (red < 0 || red > MAXVAL)
                        throw new IOException("Pixel value " + red + " outside of range [0, " + MAXVAL + "].");
                    stream.write(red);
                }
            }

        } finally {
            stream.close();
        }

    }

    public BufferedImage createGrayScaleImage() {
        final int multiplier = 2;
        final int defaultHeight = 256 * multiplier;
        final int defaultWidth = 64;
        int grayColor = 0;
        BufferedImage grayScale = new BufferedImage(defaultWidth, defaultHeight, TYPE_BYTE_GRAY);

        for (int i = 0; i < defaultHeight; i++) {
            for (int j = 0; j < defaultWidth; j++) {
                int color = ColorUtilities.createRGB(grayColor, grayColor, grayColor);
                grayScale.setRGB(j, i, color);
                //System.out.println("width: " + j + " height: " + i + " color: " +grayColor);
            }

            if (i % multiplier == 0 && i != 0) {
                grayColor++;
            }

        }
        return grayScale;
    }

    public BufferedImage createColorScaleImage() {
        final int step = 5;
        final int defaultHeight = 363;
        final int defaultWidth = 64;
        int red = 0;
        int green = 0;
        int blue = 0;
        int rgb;
        int counter = 0;
        BufferedImage colorScaleImage = new BufferedImage(defaultWidth, defaultHeight, BufferedImage.TYPE_INT_ARGB);
        int i = 0;

        //Arrancamos en Negro
        //(0,0,0)
        while (green < 256) {
            for (int j = 0; j < defaultWidth; j++) {
                rgb = ColorUtilities.createRGB(red, green, blue);
                colorScaleImage.setRGB(j, i, rgb);
            }
            System.out.println("red: " + red + " green: " + green + " blue: " + blue);
            green = green + step;
            i++;
            counter++;
        }
        green = green - step;
        //(0,255,0)
        //Terminamos en verde y vamos agregando rojo para llegar al amarillo
        while (red < 256) {
            for (int j = 0; j < defaultWidth; j++) {
                rgb = ColorUtilities.createRGB(red, green, blue);
                colorScaleImage.setRGB(j, i, rgb);
            }
            red = red + step;
            System.out.println("red: " + red + " green: " + green + " blue: " + blue);
            i++;
            counter++;
        }
        red = red - step;
        //(255,255,0)
        //Terminamos en amarillo y vamos sacando verde para llegar al rojo
        while (green > 0) {
            for (int j = 0; j < defaultWidth; j++) {
                rgb = ColorUtilities.createRGB(red, green, blue);
                colorScaleImage.setRGB(j, i, rgb);
            }
            green = green - step;
            System.out.println("red: " + red + " green: " + green + " blue: " + blue);
            i++;
            counter++;
        }
        //(255,0,0)
        //Terminamos en rojo y vamos agregando azul para llegar al magenta
        while (blue < 256) {
            for (int j = 0; j < defaultWidth; j++) {
                rgb = ColorUtilities.createRGB(red, green, blue);
                colorScaleImage.setRGB(j, i, rgb);
            }
            blue = blue + step;
            System.out.println("red: " + red + " green: " + green + " blue: " + blue);
            i++;
            counter++;
        }
        blue = blue - step;
        //(255,0,255)
        //Terminamos en magenta y vamos sacando rojo para llegar al azul
        while (red > 0) {
            for (int j = 0; j < defaultWidth; j++) {
                rgb = ColorUtilities.createRGB(red, green, blue);
                colorScaleImage.setRGB(j, i, rgb);
            }
            red = red - step;
            System.out.println("red: " + red + " green: " + green + " blue: " + blue);
            i++;
            counter++;
        }
        //(0,0,255)
        //Terminamos en azul y vamos agregando verde para llegar al cyan
        while (green < 256) {
            for (int j = 0; j < defaultWidth; j++) {
                rgb = ColorUtilities.createRGB(red, green, blue);
                colorScaleImage.setRGB(j, i, rgb);
            }
            green = green + step;
            System.out.println("red: " + red + " green: " + green + " blue: " + blue);
            i++;
            counter++;
        }
        green = green - step;
        //(0,255,255)
        //Terminamos en cyan y vamos agregando rojo para llegar al blanco
        while (red < 256) {
            for (int j = 0; j < defaultWidth; j++) {
                rgb = ColorUtilities.createRGB(red, green, blue);
                colorScaleImage.setRGB(j, i, rgb);
            }
            red = red + step;
            System.out.println("red: " + red + " green: " + green + " blue: " + blue);
            i++;
            counter++;
        }
        //(255,255,255)

        //System.out.println("counter: " + counter);
        return colorScaleImage;
    }

    public BufferedImage imageBinary(BufferedImage bimg, int threshold) {
        BufferedImage result = null;
        if (isGreyImage(bimg)) {
            result = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
            int thr = ColorUtilities.createRGB(threshold, threshold, threshold);
            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    if (bimg.getRGB(i, j) < thr) {
                        result.setRGB(i, j, ColorUtilities.createRGB(255, 255, 255));
                    } else {
                        result.setRGB(i, j, ColorUtilities.createRGB(0, 0, 0));
                    }
                }
            }
        } else {
            Alerts.showAlert("No es una imagen en escala de grises");
        }
        return result;
    }

    public double[] getAccumulatedDistribution(double[] array){
        double [] result = new double [256];
        result[0] = array[0];
        for(int i = 1;  i < 256; i++){
            result[i] = result[i-1] + array[i];
        }
        return result;
    }

    public boolean isGreyImage(BufferedImage bimg) {
        int rgb;
        int red;
        int green;
        int blue;
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                rgb = bimg.getRGB(i, j);
                red = ColorUtilities.getRed(rgb);
                green = ColorUtilities.getGreen(rgb);
                blue = ColorUtilities.getBlue(rgb);
                if (red != green || red != blue) {
                    return false;
                }
            }
        }
        return true;
    }

    public float[] averagePerBand (BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        int rgb;
        int imageSize = width * height;
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        float redAverage, greenAverage, blueAverage;
        float[] colorBandAverages = new float[3];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rgb = image.getRGB(j, i);
                redSum += ColorUtilities.getRed(rgb);
                greenSum += ColorUtilities.getGreen(rgb);
                blueSum += ColorUtilities.getBlue(rgb);
            }
        }

        redAverage = (float) redSum / imageSize;
        greenAverage = (float) greenSum / imageSize;
        blueAverage = (float) blueSum / imageSize;

        colorBandAverages[0] = redAverage;
        colorBandAverages[1] = greenAverage;
        colorBandAverages[2] = blueAverage;

        return colorBandAverages;
    }

    public BufferedImage imageContrast(BufferedImage bimg){
        BufferedImage result = null;
        int [][] matrix = this.getChannelMatrix(bimg);
        int r,g,b;
        result = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
        if (isGreyImage(bimg)) {
            int median = getMedian(matrix[1]);
            int deviation = (this.getStandardDeviation(matrix[1]));
            int R1 = median - deviation;
            int R2 = median + deviation;
            //Parametrizar de acuerdo a las pendientes de las recatas
            double pOscuros = 0.60;
            double pClaros = 3;
            //r1 mayor a s1 y r2 menor a s2
            double s1 = (int) Math.round(R1*pOscuros);
            double s2 = (int) Math.round(R2*pClaros);
            double m = (s2 - s1) / (R2 - R1);
            //lo llamo c porque el b lo usamos para rgb
            double c = s1 - (m * R1);
            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    r = ColorUtilities.getRed(bimg.getRGB(i,j));
                    g = ColorUtilities.getGreen(bimg.getRGB(i,j));
                    b = ColorUtilities.getBlue(bimg.getRGB(i,j));
                    if (bimg.getRGB(i, j) < R1) {
                        result.setRGB(i, j, ColorUtilities.createRGB((int)Math.round((pOscuros*r)),(int)Math.round(pOscuros*g),(int)Math.round(pOscuros*b)));
                    }
                    else if(bimg.getRGB(i, j) > R2){
                        result.setRGB(i, j, ColorUtilities.createRGB((int)Math.round(pClaros*r),(int)Math.round(pClaros*g), (int)Math.round(pClaros*b)));
                    }
                    else {
                        result.setRGB(i, j, ColorUtilities.createRGB((int)Math.round((m*r)+c),(int)Math.round((m*g)+c), (int)Math.round((m*b)+c)));
                    }
                }
            }
        }
        else {
            int rMedian = getMedian(matrix[0]);
            int gMedian = getMedian(matrix[1]);
            int bMedian = getMedian(matrix[2]);
            int rDeviation = (this.getStandardDeviation(matrix[0]));
            int gDeviation = (this.getStandardDeviation(matrix[1]));
            int bDeviation = (this.getStandardDeviation(matrix[2]));
            int rR1 = rMedian - rDeviation;
            int rR2 = rMedian + rDeviation;
            int gR1 = gMedian - gDeviation;
            int gR2 = gMedian + gDeviation;
            int bR1 = bMedian - bDeviation;
            int bR2 = bMedian + bDeviation;
            //Parametrizar de acuerdo a las pendientes de las rectas
            double pOscuros = 0.80;
            double pClaros = 2;
            //r1 mayor a s1 y r2 menor a s2
            double rS1 = (int) Math.round(rR1*pOscuros);
            double rS2 = (int) Math.round(rR2*pClaros);
            double gS1 = (int) Math.round(gR1*pOscuros);
            double gS2 = (int) Math.round(gR2*pClaros);
            double bS1 = (int) Math.round(bR1*pOscuros);
            double bS2 = (int) Math.round(gR2*pClaros);
            //Calculo la pendiente por cada canal
            double rM = (rS2 - rS1) / (rR2 - rR1);
            double gM = (gS2 - gS1) / (gR2 - gR1);
            double bM = (bS2 - bS1) / (bR2 - bR1);
            //Calculo la ordenada al origen por cada canal
            double rB = rS1 - (rM * rR1);
            double gB = gS1 - (gM * gR1);
            double bB = bS1 - (bM * bR1);
            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    r = ColorUtilities.getRed(bimg.getRGB(i,j));
                    g = ColorUtilities.getGreen(bimg.getRGB(i,j));
                    b = ColorUtilities.getBlue(bimg.getRGB(i,j));
                    //Estamos usando los parametros del canal rojo, hay que ver si usamos la maxima desviacion o la minima
                    if (bimg.getRGB(i, j) < rR1) {
                        result.setRGB(i, j, ColorUtilities.createRGB((int)Math.round((pOscuros*r)),(int)Math.round(pOscuros*g),(int)Math.round(pOscuros*b)));
                    }
                    else if(bimg.getRGB(i, j) > rR2){
                        result.setRGB(i, j, ColorUtilities.createRGB((int)Math.round(pClaros*r),(int)Math.round(pClaros*g), (int)Math.round(pClaros*b)));
                    }
                    else {
                        result.setRGB(i, j, ColorUtilities.createRGB((int)Math.round((rM*r)+rB),(int)Math.round((rM*g)+rB), (int)Math.round((rM*b)+rB)));
                    }
                }
            }
        }
        return result;
    }

    public int getMedian(int[] channel){
        int median = 0;
        for (int i = 0; i < channel.length; i++) {
            median = median + channel[i];
        }
        median = median/channel.length;
        return median;
    }

    public int getStandardDeviation(int[] channel){
        int deviation = 0;
        int median = this.getMedian(channel);
        for (int i = 0; i < channel.length; i++) {
            deviation = deviation + (int) Math.pow((channel[i] - median),2);
        }
        deviation = (int) Math.sqrt(deviation/channel.length);
        return deviation;
    }

}

