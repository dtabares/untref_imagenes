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

    public BufferedImage applyPrewitt(BufferedImage bimg){
        Mask horizontalMask = new Mask();
        Mask verticalMask = new Mask();

        horizontalMask.setHorizontalPrewittMask();
        verticalMask.setVericalPrewittMask();
        //Calculamos las convoluciones de cada mascara
        BufferedImage horizontalResult = applyConvolution2(bimg,horizontalMask);
        BufferedImage verticalResult = applyConvolution2(bimg,verticalMask);

        BufferedImage result = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
        int hor,ver,rgb;
        double promR,promG,promB;
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                hor = horizontalResult.getRGB(i,j);
                ver = verticalResult.getRGB(i,j);
                //Calculamos un promedio de ambas imagenes
                promR = (ColorUtilities.getRed(hor) + ColorUtilities.getRed(ver)) / 2;
                promG = (ColorUtilities.getGreen(hor) + ColorUtilities.getGreen(ver)) / 2;
                promB = (ColorUtilities.getBlue(hor) + ColorUtilities.getBlue(ver)) / 2;
                double norm = (Math.sqrt(Math.pow(ColorUtilities.getRed(hor),2) + Math.pow(ColorUtilities.getRed(ver),2)));
                System.out.println("r: " +promR + " g: " + promG + " b:" + promB);
                rgb = ColorUtilities.createRGB((int) Math.round(promR),(int) Math.round(promG),(int) Math.round(promB));
                //rgb = (int) Math.sqrt(Math.pow(horizontalResult.getRGB(i,j),2) + Math.pow(verticalResult.getRGB(i,j),2));
                result.setRGB(i,j,rgb);
            }
        }
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
                //System.out.println("r: " +red + " g: " + green + " b:" + blue);
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
                        redChannel[i][j] = (int) Math.round(this.imageUtilities.linearTransformation(redChannel[i][j], max, min));
                        greenChannel[i][j] = (int) Math.round(this.imageUtilities.linearTransformation(greenChannel[i][j], max, min));
                        blueChannel[i][j] = (int) Math.round(this.imageUtilities.linearTransformation(blueChannel[i][j], max, min));
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

    private BufferedImage applyConvolution2(BufferedImage bimg, Mask mask){
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
                //System.out.println("r: " +red + " g: " + green + " b:" + blue);
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

                    if(redChannel[i][j]>255){
                        redChannel[i][j] = (int)this.imageUtilities.linearTransformation(redChannel[i][j], max, min);
                        greenChannel[i][j] = (int)this.imageUtilities.linearTransformation(greenChannel[i][j], max, min);
                        blueChannel[i][j] = (int)this.imageUtilities.linearTransformation(blueChannel[i][j], max, min);
                    }
                    //Trunco los valores menores a cero SOLO cuando se van de rango
                    if(redChannel[i][j]<0){
                        redChannel[i][j] = 0;
                        greenChannel[i][j] = 0;
                        blueChannel[i][j] = 0;
                    }
//                        Aplico T. Lineal
//                        redChannel[i][j] = (int) Math.round(this.imageUtilities.linearTransformation(redChannel[i][j], max, min));
//                        greenChannel[i][j] = (int) Math.round(this.imageUtilities.linearTransformation(greenChannel[i][j], max, min));
//                        blueChannel[i][j] = (int) Math.round(this.imageUtilities.linearTransformation(blueChannel[i][j], max, min));
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
