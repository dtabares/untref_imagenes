package sample;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.util.Scanner;

import static javafx.scene.paint.Color.rgb;

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

    public BufferedImage imageSubstraction(BufferedImage bimg1, BufferedImage bimg2)
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
        int min = this.getMinRgb(bimg);
        int max = this.getMaxRgb(bimg);
        int range = max - min;
        try
        {
            temp = new BufferedImage(bimg.getWidth(),bimg.getHeight(),bimg.getType());

            for (int i = 0; i < bimg.getWidth(); i++)
            {
                for( int j = 0; j < bimg.getHeight(); j++)
                {
                    int p = (bimg.getRGB(i,j));
                    if(p<0)
                    {
                        temp.setRGB(i,j,(-1)* ((p*-1)*((int)Math.log(alpha/100))));
                    }
                    else
                    {
                        temp.setRGB(i,j,(p*(int)Math.log(alpha/100)));
                    }
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
