package sample;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
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
        int[][] gradient = new int[bimg.getWidth()][bimg.getHeight()];

        for (int i = 0; i < gradient.length; i++) {
            for (int j = 0; j < gradient[0].length; j++) {
                gradient[i][j] = 0;
            }
        }

        horizontalMask.setHorizontalPrewittMask();
        verticalMask.setVericalPrewittMask();
        //Calculamos las convoluciones de cada mascara
        int[][] horizontalResult = applyConvolutionReloaded(bimg,horizontalMask);
        int[][] verticalResult = applyConvolutionReloaded(bimg,verticalMask);

        BufferedImage result = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                int norm = (int) Math.sqrt(Math.pow(horizontalResult[i][j],2) + Math.pow(verticalResult[i][j],2));
                //System.out.println("hor: " +hor + " ver: " + ver +" hgrey: " +hgrey + " vgrey: " + vgrey + " norm:" + norm);
                gradient[i][j] = norm;

            }
        }
        int[] minMax = this.imageUtilities.findGreyMinMaxValues(gradient);
        int min = minMax[0];
        int max = minMax[1];

        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                int p = (int) this.imageUtilities.linearTransformation(gradient[i][j],max,min);
                result.setRGB(i,j,ColorUtilities.createRGB(p,p,p));
            }
        }
        return result;
    }

    public BufferedImage applySobel(BufferedImage bimg){
        Mask horizontalMask = new Mask();
        Mask verticalMask = new Mask();
        int[][] gradient = new int[bimg.getWidth()][bimg.getHeight()];

        horizontalMask.setHorizontalSobelMask();
        verticalMask.setVericalSobelMask();
        //Calculamos las convoluciones de cada mascara
        int[][] horizontalResult = applyConvolutionReloaded(bimg,horizontalMask);
        int[][] verticalResult = applyConvolutionReloaded(bimg,verticalMask);

        BufferedImage result = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                int norm = (int) Math.sqrt(Math.pow(horizontalResult[i][j],2) + Math.pow(verticalResult[i][j],2));
                //System.out.println("hor: " +hor + " ver: " + ver +" hgrey: " +hgrey + " vgrey: " + vgrey + " norm:" + norm);
                gradient[i][j] = norm;

            }
        }
        int[] minMax = this.imageUtilities.findGreyMinMaxValues(gradient);
        int min = minMax[0];
        int max = minMax[1];

        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                int p = (int) this.imageUtilities.linearTransformation(gradient[i][j],max,min);
                result.setRGB(i,j,ColorUtilities.createRGB(p,p,p));
            }
        }
        return result;
    }

    public BufferedImage applyLaplace(BufferedImage bimg, Boolean zeroCrossing){
        BufferedImage result = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
        Mask laplaceMask = new Mask();
        laplaceMask.setLaplaceMask();
        //Aplico la mascara de Laplace con una convolucion
        int[][] matrixResult = applyConvolutionReloaded(bimg,laplaceMask);
        if(zeroCrossing){
            //Aplico metodo de Zero Crossing
            int [][] zeroMatrix = applyZeroCrossingEdgeDetection(matrixResult);
            //Recorro la buffered image y la relleno con el resultado Laplace + Zero Crossing
            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    result.setRGB(i,j,ColorUtilities.createRGB(zeroMatrix[i][j],zeroMatrix[i][j],zeroMatrix[i][j]));
                }
            }
            return result;
        }
        // Aplico y devuelvo solo el filtro de Laplace
        int[] minMax = this.imageUtilities.findGreyMinMaxValues(matrixResult);
        int min = minMax[0];
        int max = minMax[1];
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                int p = (int) this.imageUtilities.linearTransformation(matrixResult[i][j],max,min);
                result.setRGB(i,j,ColorUtilities.createRGB(p,p,p));
            }
        }
        return result;
    }

    public int[][] applyZeroCrossingEdgeDetection(int[][] imageMatrix){
        int[][] resultMatrix = new int[imageMatrix.length][imageMatrix[0].length];
        //Recorro la matriz hasta la anteultima columna
        for (int i = 0; i < imageMatrix.length-1; i++) {
            for (int j = 0; j < imageMatrix[0].length; j++) {
                int currentPixel = imageMatrix[i][j];
                int rightPixel = imageMatrix[i+1][j];
                //Comparo el pixel a izq y a dcha, si hay un cambio de signo pongo un blanco
                if ( (currentPixel > 0 && rightPixel < 0) || (currentPixel < 0 && rightPixel > 0)){
                    resultMatrix[i][j] = 255;
                }
                //Tengo que ver que hay a mis costados (teniendo cuidado de que i NO sea el elemento 0 del array)
                else if(currentPixel == 0 && i > 0){
                    int leftPixel = imageMatrix[i -1][j];
                    if((leftPixel > 0 && rightPixel < 0) || (leftPixel < 0 && rightPixel > 0)){
                        resultMatrix[i][j] = 255;
                    }
                    else{
                        resultMatrix[i][j] = 0;
                    }
                }
                else{
                    resultMatrix[i][j] = 0;
                }
            }
        }
        return resultMatrix;
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
            System.out.println("min: " + min + " max: " + max);
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

    private int[][] applyConvolutionReloaded(BufferedImage bimg, Mask mask){
        int grey;
        Image image = new Image(bimg);
        image.convertToGreyDataMatrix();
        int[][] greyDataMatrix = image.getGreyDataMatrix();
        int[][] result = new int[image.getWidth()][image.getHeight()];
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                result[i][j] = 0;
            }
        }
        int widthLimit = image.getWidth() - mask.getSize();
        int heightLimit = image.getHeight() - mask.getSize();
        grey = 0;

        for (int i = 0; i <= widthLimit; i++) {
            for (int j = 0; j <= heightLimit; j++) {
                for (int k = 0; k < mask.getSize(); k++) {
                    for (int l = 0; l < mask.getSize(); l++) {
                        grey += (greyDataMatrix[i+k][j+l] * mask.getValue(k,l));
                    }
                }
                result[i + mask.getCenter()][j + mask.getCenter()] = grey;
                //System.out.println("r: " +grey);
                grey = 0;

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
