package sample;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


import static javafx.scene.paint.Color.rgb;

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

                    int rgb = (p << 24) | (p << 16) | (p << 8) | p;

                    bimg.setRGB(j, i, rgb);
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

    public BufferedImage imageAddition(BufferedImage bimg1, BufferedImage bimg2)
    {
        BufferedImage temp = null;
        try
        {
            if (bimg1.getType() == bimg2.getType())
            {
                boolean firstIsWidest = bimg1.getWidth() > bimg2.getWidth();
                boolean firstIsHigher = bimg1.getHeight() > bimg2.getHeight();
                int maxWidth;
                int minWidth;
                int maxHeight;
                int minHeight;
                if (bimg1.getWidth() > bimg2.getWidth())
                {
                    maxWidth = bimg1.getWidth();
                    minWidth = bimg2.getWidth();
                }
                else
                {
                    maxWidth = bimg2.getWidth();
                    minWidth = bimg1.getWidth();
                }
                if(bimg1.getHeight() > bimg2.getHeight())
                {
                    maxHeight = bimg1.getHeight();
                    minHeight = bimg2.getHeight();
                }
                else
                {
                    maxHeight = bimg2.getHeight();
                    minHeight = bimg1.getHeight();
                }
                temp = new BufferedImage(maxWidth,maxHeight,bimg1.getType());

                for (int i = 0; i < maxWidth; i++)
                {
                    for( int j = 0; j < maxHeight; j++)
                    {
                        if (i < minWidth && j < minHeight)
                        {
                            temp.setRGB(i,j,bimg1.getRGB(i,j) + bimg2.getRGB(i,j));
                        }
                        else if (i < minWidth && j >= minHeight)
                        {
                            if(firstIsHigher)
                            {
                                temp.setRGB(i,j,bimg1.getRGB(i,j));
                            }
                            else
                            {
                                temp.setRGB(i,j,bimg2.getRGB(i,j));
                            }
                        }
                        else if (i >= minWidth && j < minHeight)
                        {
                            if(firstIsWidest)
                            {
                                temp.setRGB(i,j,bimg1.getRGB(i,j));
                            }
                            else
                            {
                                temp.setRGB(i,j,bimg2.getRGB(i,j));
                            }
                        }
                        else
                        {
                            temp.setRGB(i,j,0	);
                        }
                    }

                }
            }
            else
            {
                Alerts.showAlert("No se pueden sumar formatos diferentes");
            }
        }
        catch (Exception e)
        {
            Alerts.showAlert(e.getMessage());
        }

        return temp;
    }

    public BufferedImage imageSubtraction(BufferedImage bimg1, BufferedImage bimg2)
    {
        BufferedImage temp = null;
        try
        {
            if (bimg1.getType() == bimg2.getType())
            {
                temp = new BufferedImage(bimg1.getWidth(),bimg1.getHeight(),bimg1.getType());

                for (int i = 0; i < bimg1.getWidth(); i++)
                {
                    for( int j = 0; j < bimg1.getHeight(); j++)
                    {
                        temp.setRGB(i,j,bimg1.getRGB(i,j) - bimg2.getRGB(i,j));
                    }

                }
            }
            else
            {
                Alerts.showAlert("No se pueden sumar formatos diferentes");
            }
        }
        catch (Exception e)
        {
            Alerts.showAlert(e.getMessage());
        }
        return temp;
    }

    public BufferedImage imageScalarProduct(BufferedImage bimg, int scalar)
    {
        BufferedImage temp = null;
        try
        {
            temp = new BufferedImage(bimg.getWidth(),bimg.getHeight(),bimg.getType());

            for (int i = 0; i < bimg.getWidth(); i++)
            {
                for( int j = 0; j < bimg.getHeight(); j++)
                {
                        temp.setRGB(i,j,bimg.getRGB(i,j)*scalar);
                }
            }
        }
        catch (Exception e)
        {
            Alerts.showAlert(e.getMessage());
        }
        return temp;
    }

    public BufferedImage dynamicRangeCompression(BufferedImage bimg, int alpha)
    {
        BufferedImage temp = null;
/*        int min = this.getMinRgb(bimg);
        int max = this.getMaxRgb(bimg);
        int range = max - min;*/
        try
        {
            temp = new BufferedImage(bimg.getWidth(),bimg.getHeight(),bimg.getType());

            for (int i = 0; i < bimg.getWidth(); i++)
            {
                for( int j = 0; j < bimg.getHeight(); j++)
                {
                    int p = (bimg.getRGB(i,j));
                    int red = (p >> 16) & 0xFF;
                    int green = (p >> 8) & 0xFF;
                    int blue = p & 0xFF;
                    red = (100/alpha) * (int)Math.round(Math.log1p((double) (1 + red)));
                    green = (100/alpha) * (int)Math.round(Math.log1p((double) (1 + green)));
                    blue = (100/alpha) * (int)Math.round(Math.log1p((double) (1 + blue)));
                    int rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                    temp.setRGB(i,j,rgb);
                }
            }
        }
        catch (Exception e)
        {
            Alerts.showAlert(e.getMessage());
        }
        return temp;
    }

    public BufferedImage imagePow(BufferedImage bimg, int gamma)
    {
        BufferedImage temp = null;
        try
        {
            temp = new BufferedImage(bimg.getWidth(),bimg.getHeight(),bimg.getType());

            for (int i = 0; i < bimg.getWidth(); i++)
            {
                for( int j = 0; j < bimg.getHeight(); j++)
                {
                    int p = (bimg.getRGB(i,j));
                    int red = (p >> 16) & 0xFF;
                    int green = (p >> 8) & 0xFF;
                    int blue = p & 0xFF;
                    red = (int)Math.round(Math.pow((double) (1 + red), gamma));
                    green = (int)Math.round(Math.pow((double) (1 + green), gamma));
                    blue = (int)Math.round(Math.pow((double) (1 + blue), gamma));
                    int rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                    temp.setRGB(i,j,rgb);
                }
            }
        }
        catch (Exception e)
        {
            Alerts.showAlert(e.getMessage());
        }
        return temp;
    }

    public BufferedImage imageNegative(BufferedImage bimg)
    {
        BufferedImage temp = null;
        try
        {
            temp = new BufferedImage(bimg.getWidth(),bimg.getHeight(),bimg.getType());

            for (int i = 0; i < bimg.getWidth(); i++)
            {
                for( int j = 0; j < bimg.getHeight(); j++)
                {
                    int p = (bimg.getRGB(i,j));
                    int red = 255 - ((p >> 16) & 0xFF);
                    int green = 255 - ((p >> 8) & 0xFF);
                    int blue = 255 - (p & 0xFF);
                    int rgb = ((red&0x0ff)<<16)|((green&0x0ff)<<8)|(blue&0x0ff);
                    temp.setRGB(i,j,rgb);
                }
            }
        }
        catch (Exception e)
        {
            Alerts.showAlert(e.getMessage());
        }
        return temp;
    }

    public int getMaxRgb (BufferedImage bimg)
    {
        int max = 0;
        for (int i = 0; i < bimg.getWidth(); i++)
        {
            for( int j = 0; j < bimg.getHeight(); j++)
            {
                int temp = bimg.getRGB(i,j);
                if( temp > max)
                {
                    max = temp;
                }
            }
        }
        return max;
    }

    public int getMinRgb (BufferedImage bimg)
    {
        int min = 255;
        for (int i = 0; i < bimg.getWidth(); i++)
        {
            for( int j = 0; j < bimg.getHeight(); j++)
            {
                int temp = bimg.getRGB(i,j);
                if( temp < min)
                {
                    min = temp;
                }
            }
        }
        return min;
    }
}
