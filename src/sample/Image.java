package sample;

import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;

public class Image {

    // Full Image attributes
    private BufferedImage bufferedImage;
    private int[][] dataMatrix;
    private int width;
    private int height;
    private int imageType;
    private boolean splittedInRGBbands;
    private boolean splittedInHSVbands;

    // RGB Band Attributes
    private BufferedImage redBufferedImageChannel;
    private BufferedImage greenBufferedImageChannel;
    private BufferedImage blueBufferedImageChannel;
    private int[][] redDataMatrixChannel;
    private int[][] greenDataMatrixChannel;
    private int[][] blueDataMatrixChannel;

    // HSV Band Attributes
    private BufferedImage hueBufferedImageChannel;
    private BufferedImage saturationBufferedImageChannel;
    private BufferedImage valueBufferedImageChannel;
    // Por ahora estos 3 no los estoy usando. No tuvimos la necesidad
    private int[][] hueDataMatrixChannel;
    private int[][] saturationDataMatrixChannel;
    private int[][] valueDataMatrixChannel;

    public Image(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        this.height = bufferedImage.getHeight();
        this.width = bufferedImage.getWidth();
        this.imageType = bufferedImage.getType();
        this.dataMatrix = new int[this.width][this.height];
        this.bufferedImageToDataMatrix();
        this.splittedInRGBbands = false;
        this.splittedInHSVbands = false;

    }

    public Image(int[][] dataMatrix, int imageType) {
        this.dataMatrix = dataMatrix;
        this.imageType = imageType;
        this.width = dataMatrix.length;
        this.height = dataMatrix[0].length;
        this.bufferedImage = new BufferedImage(this.width,this.height,imageType);
        this.dataMatrixToBufferedImage();
        this.splittedInRGBbands = false;
        this.splittedInHSVbands = false;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public int[][] getDataMatrix() {
        return dataMatrix;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getImageType() {
        return imageType;
    }

    private void bufferedImageToDataMatrix(){
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                this.dataMatrix[i][j] = this.bufferedImage.getRGB(i,j);
            }
        }
    }

    private void dataMatrixToBufferedImage(){
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                this.bufferedImage.setRGB(i,j,this.dataMatrix[i][j]);
            }
        }
    }

    private void splitInRGBcolorBands(){
        //Initialize channels
        this.redBufferedImageChannel = new BufferedImage(this.width,this.height,imageType);
        this.greenBufferedImageChannel = new BufferedImage(this.width,this.height,imageType);
        this.blueBufferedImageChannel = new BufferedImage(this.width,this.height,imageType);
        this.redDataMatrixChannel = new int[this.width][this.height];
        this.greenDataMatrixChannel = new int[this.width][this.height];
        this.blueDataMatrixChannel = new int[this.width][this.height];

        //aux variables
        int rgb, red, green, blue;

        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                rgb = this.bufferedImage.getRGB(j, i);
                red = ColorUtilities.getRed(rgb);
                green = ColorUtilities.getGreen(rgb);
                blue = ColorUtilities.getBlue(rgb);

                this.redBufferedImageChannel.setRGB(j, i, ColorUtilities.createRGB(red, 0, 0));
                this.redDataMatrixChannel[j][i] = red;
                this.greenBufferedImageChannel.setRGB(j, i, ColorUtilities.createRGB(0, green, 0));
                this.greenDataMatrixChannel[j][i] = green;
                this.blueBufferedImageChannel.setRGB(j, i, ColorUtilities.createRGB(0, 0, blue));
                this.blueDataMatrixChannel[j][i] = blue;
            }
        }
        this.splittedInRGBbands = true;

    }

    private void splitInHSVBands(){
        //Initialize channels
        this.hueBufferedImageChannel = new BufferedImage(this.width, this.height, TYPE_BYTE_GRAY);
        this.saturationBufferedImageChannel = new BufferedImage(this.width, this.height, TYPE_BYTE_GRAY);
        this.valueBufferedImageChannel = new BufferedImage(this.width, this.height, TYPE_BYTE_GRAY);

        this.hueDataMatrixChannel = new int[this.width][this.height];
        this.saturationDataMatrixChannel = new int[this.width][this.height];
        this.valueDataMatrixChannel = new int[this.width][this.height];

        //aux variables
        int rgb;
        int[] hueOnly, saturationOnly, valueOnly;
        float[] hsv;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rgb = this.bufferedImage.getRGB(j, i);
                hsv = ColorUtilities.RGBtoHSV(rgb);
                hueOnly = ColorUtilities.HSVtoRGB(hsv[0], hsv[0], hsv[0]);
                saturationOnly = ColorUtilities.HSVtoRGB(hsv[1], hsv[1], hsv[1]);
                valueOnly = ColorUtilities.HSVtoRGB(hsv[2], hsv[2], hsv[2]);
                this.hueBufferedImageChannel.setRGB(j, i, ColorUtilities.createRGB(hueOnly[0], hueOnly[1], hueOnly[2]));
                this.saturationBufferedImageChannel.setRGB(j, i, ColorUtilities.createRGB(saturationOnly[0], saturationOnly[1], saturationOnly[2]));
                this.valueBufferedImageChannel.setRGB(j, i, ColorUtilities.createRGB(valueOnly[0], valueOnly[1], valueOnly[2]));
            }
        }

        this.splittedInHSVbands = true;
    }

    public BufferedImage getRedBufferedImageChannel() {
        if (this.splittedInRGBbands == false)
        {
            this.splitInRGBcolorBands();
        }
        return redBufferedImageChannel;
    }

    public BufferedImage getGreenBufferedImageChannel() {
        if (this.splittedInRGBbands == false)
        {
            this.splitInRGBcolorBands();
        }
        return greenBufferedImageChannel;
    }

    public BufferedImage getBlueBufferedImageChannel() {
        if (this.splittedInRGBbands == false)
        {
            this.splitInRGBcolorBands();
        }
        return blueBufferedImageChannel;
    }

    public BufferedImage getHueBufferedImageChannel() {
        if (this.splittedInHSVbands == false)
        {
            this.splitInHSVBands();
        }
        return hueBufferedImageChannel;
    }

    public BufferedImage getSaturationBufferedImageChannel() {
        if (this.splittedInHSVbands == false)
        {
            this.splitInHSVBands();
        }
        return saturationBufferedImageChannel;
    }

    public BufferedImage getValueBufferedImageChannel() {
        if (this.splittedInHSVbands == false)
        {
            this.splitInHSVBands();
        }
        return valueBufferedImageChannel;
    }

    public int[][] getRedDataMatrixChannel() {
        if (this.splittedInRGBbands == false)
        {
            this.splitInRGBcolorBands();
        }
        return redDataMatrixChannel;
    }

    public int[][] getGreenDataMatrixChannel() {
        if (this.splittedInRGBbands == false)
        {
            this.splitInRGBcolorBands();
        }
        return greenDataMatrixChannel;
    }

    public int[][] getBlueDataMatrixChannel() {
        if (this.splittedInRGBbands == false)
        {
            this.splitInRGBcolorBands();
        }
        return blueDataMatrixChannel;
    }
}
