package sample;

import java.awt.image.BufferedImage;

public class Threshold {

    public static BufferedImage applyGlobalThresholding(BufferedImage bimg, int predefinedDelta, Controller c){
        Image image = new Image(bimg);
        ImageUtilities imgUtils = new ImageUtilities();
        int[] minMax;
        int thresholdValue, oldThresholdValue;
        int min, max;
        int totalPixels = bimg.getHeight() * bimg.getWidth();
        int whitePixels, blackPixels;
        int delta;
        int[][] tempResult;
        int iteration = 0;

        //Paso la imagen a escala de grises
        image.convertToGreyDataMatrix();
        minMax = imgUtils.findGreyMinMaxValues(image.getGreyDataMatrix());
        min = minMax[0];
        max = minMax[1];
        //Seteo el umbral inicial
        thresholdValue = ((max - min)/2);

        //Itero mientras la diferencia entre 2 umbrales (T) sea mayor al delta predefinido
        do {
            iteration++;
            //Aplico umbralizacion
            tempResult = Threshold.applyThreshold(image.getGreyDataMatrix(),thresholdValue,image.getWidth(),image.getHeight());
            //Cuento los blancos y los negros
            whitePixels = Threshold.countWhitePixels(tempResult, image.getWidth(),image.getHeight());
            blackPixels = totalPixels - whitePixels;
            oldThresholdValue = thresholdValue;
            //Calculo el nuevo valor de Threshold
            thresholdValue = Threshold.calculateNewThreshold(image,tempResult,whitePixels,blackPixels);
            delta = Math.abs(thresholdValue - oldThresholdValue);
            System.out.println("iteration: " + iteration + " delta: " + delta + " oldT: " + oldThresholdValue + " T: " + thresholdValue);
        } while (predefinedDelta < delta);

        tempResult = Threshold.applyThreshold(image.getGreyDataMatrix(),thresholdValue,image.getWidth(),image.getHeight());
        Image temp = new Image(tempResult);

        c.setBottomText("T: " + thresholdValue + "      -  iterations: " + iteration);
        return temp.getBufferedImage();
    }

    private static int[][] applyThreshold(int[][] imageData, int thresholdValue, int imageWidth, int imageHeight){
        int[][] result = new int[imageWidth][imageHeight];

        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                if(imageData[i][j] <= thresholdValue){
                    result[i][j] = 0;
                }
                else {
                    result[i][j] = 255;
                }
            }
        }

        return result;
    }

    private static int countWhitePixels(int[][] imageData, int imageWidth, int imageHeight){
        int whitePixels = 0;

        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                if(imageData[i][j] == 255){
                    whitePixels++;
                }
            }
        }

        return whitePixels;
    }

    private static int calculateNewThreshold(Image image, int[][] thresholdedImage, int whitePixels, int blackPixels){
        double m1;
        double m2;
        int threshold;
        int whiteSum = 0;
        int blackSum = 0;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (thresholdedImage[i][j] == 0){
                    blackSum = blackSum + image.getGreyValue(i,j);
                }
                else{
                    whiteSum = whiteSum + image.getGreyValue(i,j);
                }
            }
        }
        m1 = ((1.0/blackPixels) * (double) blackSum);
        m2 = ((1.0/whitePixels) * (double) whiteSum);
        threshold = (int) (m1 + m2)/2;
        return threshold;
    }
}
