package sample;

import java.awt.image.BufferedImage;

public class NoiseGenerator {

    public static int[][] generateMultiplicativeExponentialNoiseMatrix(int width, int height, double lambda, int affectedPixelPercentage){
        int imageSize = width * height;
        int[][] noiseMatrix = new int[width][height];
        int affectedPixels = (int) (imageSize * affectedPixelPercentage / 100);
        int affectationCoefficient = imageSize/affectedPixels;
        int pixelCount = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (pixelCount % affectationCoefficient == 0){
                    noiseMatrix[i][j] = NumberGenerator.generateRandomExponentialNumber(lambda);
                }
                else{
                    noiseMatrix[i][j] = 1;
                }
                pixelCount++;
            }
        }

        return  noiseMatrix;
    }



}
