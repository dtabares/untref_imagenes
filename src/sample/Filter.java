package sample;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Filter {

    ImageUtilities imageUtilities = new ImageUtilities();

    public BufferedImage applyMeanFilter(BufferedImage bimg, int maskSize) {
        Mask mask = new Mask(maskSize);
        mask.setMeanMask();
        BufferedImage result = applyConvolution(bimg, mask);
        return result;
    }

    public  BufferedImage applyMedianFilter(BufferedImage bimg, int maskSize){
        Mask mask = new Mask(maskSize);
        mask.setMedianMask();
        BufferedImage result = applyMedianConvolution(bimg, mask);
        return result;
    }

    public  BufferedImage applyWeightedMedianFilter(BufferedImage bimg){
        Mask mask = new Mask(3);
        mask.setWeightedMedianMask();
        BufferedImage result = applyMedianConvolution(bimg, mask);
        return result;
    }

    public BufferedImage applyGaussFilter(BufferedImage bimg, double sigma){
        int maskSize = (int) Math.round(2*sigma+1);
        Mask mask = new Mask(maskSize);
        mask.setGaussMask(sigma);
        BufferedImage result = applyConvolution(bimg, mask);
        mask = null;
        return result;
    }

    public  BufferedImage enhanceEdges(BufferedImage bimg, int maskSize){
        Mask mask = new Mask(maskSize);
        mask.setHighPassFilterMask();
        BufferedImage result = applyConvolution(bimg,mask);
        return result;
    }

    private BufferedImage applyConvolution(BufferedImage bimg, Mask mask){
        int rgb, red, green, blue;
        BufferedImage result = imageUtilities.copyImageIntoAnother(bimg);
        int widthLimit = bimg.getWidth() - mask.getSize();
        int heightLimit = bimg.getHeight() - mask.getSize();
        Image temp = new Image(bimg);
        int[][] redChannel = temp.getRedDataMatrixChannel();
        int[][] greenChannel = temp.getGreenDataMatrixChannel();
        int[][] blueChannel = temp.getBlueDataMatrixChannel();
        red = 0;
        green = 0;
        blue = 0;
        int max = 0;
        int min = 255;


        for (int i = 0; i <= widthLimit; i++) {
            for (int j = 0; j <= heightLimit; j++) {
                for (int k = 0; k < mask.getSize(); k++) {
                    for (int l = 0; l < mask.getSize(); l++) {
                        rgb = bimg.getRGB(i+k,j+l);
                        red += (ColorUtilities.getRed(rgb) * mask.getValue(k,l));
                        green += (ColorUtilities.getGreen(rgb) * mask.getValue(k,l));
                        blue += (ColorUtilities.getBlue(rgb) * mask.getValue(k,l));
                    }
                }
                redChannel[i + mask.getCenter()][j + mask.getCenter()] = red;
                greenChannel[i + mask.getCenter()][j + mask.getCenter()] = green;
                blueChannel[i + mask.getCenter()][j + mask.getCenter()] = blue;
                System.out.println("r: " +red + " g: " + green + " b:" + blue);
                if(red > max){
                    max = red;
                }
                if(green > max){
                    max = green;
                }
                if(blue > max){
                    max = blue;
                }

                if(red < min){
                    min = red;
                }
                if(green < min){
                    min = green;
                }
                if(blue < min){
                    min = blue;
                }
                red = 0;
                green = 0;
                blue = 0;
            }
        }

        if(max > 255 || min < 0){
            for (int i = 0; i < result.getWidth(); i++) {
                for (int j = 0; j < result.getHeight(); j++) {
                    redChannel[i][j] = (int) this.imageUtilities.linearTransformation(redChannel[i][j],max,min);
                    greenChannel[i][j] = (int) this.imageUtilities.linearTransformation(greenChannel[i][j],max,min);
                    blueChannel[i][j] = (int) this.imageUtilities.linearTransformation(blueChannel[i][j],max,min);
                }
            }
        }

        // Relleno la imagen a devolver
        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {
                result.setRGB(i,j,ColorUtilities.createRGB(redChannel[i][j], greenChannel[i][j], blueChannel[i][j]));
            }
        }
        return result;
    }

    private BufferedImage applyMedianConvolution(BufferedImage bimg, Mask mask){
        int rgb, red, green, blue;
        BufferedImage result = imageUtilities.copyImageIntoAnother(bimg);
        int widthLimit = bimg.getWidth() - mask.getSize();
        int heightLimit = bimg.getHeight() - mask.getSize();
        Image temp = new Image(bimg);
        int[][] redChannel = temp.getRedDataMatrixChannel();
        int[][] greenChannel = temp.getGreenDataMatrixChannel();
        int[][] blueChannel = temp.getBlueDataMatrixChannel();
        int[] redMedianArray = new int[mask.getMaskSum()];
        int[] greenMedianArray = new int[mask.getMaskSum()];
        int[] blueMedianArray = new int[mask.getMaskSum()];
        int rMed, gMed, bMed;
        red = 0;
        green = 0;
        blue = 0;
        int counter = 0;
        for (int i = 0; i <= widthLimit; i++) {
            for (int j = 0; j <= heightLimit; j++) {
                for (int k = 0; k < mask.getSize(); k++) {
                    for (int l = 0; l < mask.getSize(); l++) {
                        for (int m = 0; m < mask.getValue(k,l); m++){
                            rgb = bimg.getRGB(i+k,j+l);
                            redMedianArray[counter] = ColorUtilities.getRed(rgb);
                            greenMedianArray[counter] = ColorUtilities.getGreen(rgb);
                            blueMedianArray[counter] = ColorUtilities.getBlue(rgb);
                            counter++;
                        }
                    }
                }
                counter = 0;

                Arrays.sort(redMedianArray);
                Arrays.sort(greenMedianArray);
                Arrays.sort(blueMedianArray);

                rMed = redMedianArray[(int)Math.floor((double)redMedianArray.length/2)];
                gMed = greenMedianArray[(int)Math.floor((double)greenMedianArray.length/2)];
                bMed = blueMedianArray[(int)Math.floor((double)blueMedianArray.length/2)];

                redChannel[i + mask.getCenter()][j + mask.getCenter()] = rMed;
                greenChannel[i + mask.getCenter()][j + mask.getCenter()] = gMed;
                blueChannel[i + mask.getCenter()][j + mask.getCenter()] = bMed;

                red = 0;
                green = 0;
                blue = 0;
            }
        }

        // Relleno la imagen a devolver
        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {
                result.setRGB(i,j,ColorUtilities.createRGB(redChannel[i][j], greenChannel[i][j], blueChannel[i][j]));
            }
        }
        return result;
    }

}
