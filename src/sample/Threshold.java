package sample;

import java.awt.image.BufferedImage;
import java.util.ArrayList;


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


    public static BufferedImage appyOtsu(BufferedImage bimg, Controller c){
        Image image = new Image(bimg);
        //Paso la imagen a escala de grises
        image.convertToGreyDataMatrix();

        //Obtengo el histograma normalizado
        double[] normalizedHistogram = Histogram.getNormalizedHistogram(image.getGreyDataMatrix());

        //Calculo las sumas acumulativas
        double[] cumulativeSums = Threshold.calculateCumulativeSums(normalizedHistogram);

        //Calculo las medias acumulativas
        double[] cumulativeMeans = Threshold.calculateCumulativeMeans(normalizedHistogram);

        //Calculo la Media Global
        double globalMean = Threshold.calculateGlobalMean(normalizedHistogram);

        //Calculo varianza entre clases
        double[] variances = Threshold.calculateVariance(globalMean,cumulativeSums,cumulativeMeans);

        //Obtengo la maxima varianza (umbral optimo T)
        int threshold = Threshold.optimizeVariance(variances);

        //Umbralizo con ese valor de T
        int[][] thresholdedImage = Threshold.applyThreshold(image.getGreyDataMatrix(),threshold,image.getWidth(),image.getHeight());

        Image temp = new Image(thresholdedImage);
        c.setBottomText("T: " + threshold);
        return temp.getBufferedImage();
    }

    private static double[] calculateCumulativeSums(double[] normalizedHistogram){
        double[] cumulativeSums = new double[256];
        double sum;
        for (int i = 0; i < 256; i++) {
            sum = 0;
            for (int j = 0; j <= i; j++) {
                sum = sum + normalizedHistogram[j];
            }
            cumulativeSums[i] = sum;
        }

        return cumulativeSums;
    }

    private static double[] calculateCumulativeMeans(double[] normalizedHistogram){
        double[] cumulativeMeans = new double[256];
        double temp;

        for (int i = 0; i < 256; i++) {
            temp = 0;
            for (int j = 0; j <= i; j++) {
                temp = temp + (normalizedHistogram[j] * j);
            }
            cumulativeMeans[i] = temp;
        }
        return cumulativeMeans;
    }

    private static double calculateGlobalMean(double[] normalizedHistogram){
        double globalMean = 0;

        for (int i = 0; i < 256; i++) {
            globalMean = globalMean + (normalizedHistogram[i] * i);
        }
        return globalMean;
    }

    private static double[] calculateVariance(double globalMean, double[] cumulativeSums, double[] cumulativeMeans){
        //Lo creo como array list porque no se cuantos elementos va a tener de antemano
        //ArrayList<Double> variancesTemp = new ArrayList<Double>();
        double[] variances = new double[256];
        double numerator;
        double denominator;

        for (int i = 0; i < 256; i++) {
            numerator = Math.pow(((globalMean * cumulativeSums[i]) - cumulativeMeans[i]),2);
            denominator = (cumulativeSums[i] * (1 - cumulativeSums[i]));
            if(denominator > 0.0){
                variances[i] = (numerator/denominator);
            }
            else{
                variances[i] = 0;
            }
        }

        return variances;
    }

    private static int optimizeVariance(double[] variance){
        int threshold;
        double max = variance[0];
        int maxTPos = 0;
        boolean unique = true;
        int occurrences = 0;

        for (int i = 1; i < variance.length; i++) {
            if(variance[i] > max){
                max = variance[i];
                maxTPos = i;
            }
        }

        for (int i = 0; i < variance.length; i++) {
            if(variance[i] == max){
                occurrences++;
            }
        }

        if(occurrences > 1){
            unique = false;
        }


        if(unique){
            //threshold = (int) Math.round(max);
            threshold = maxTPos;
        }
        else{
            double sum = 0;
            for (int i = 0; i < variance.length; i++) {
                if(variance[i] == max){
                    sum = sum + i;
                }
            }
            threshold = (int) Math.round(sum/occurrences);
        }

        return threshold;
    }

}
