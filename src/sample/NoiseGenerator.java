package sample;


public class NoiseGenerator {

    public static int[][] generateMultiplicativeExponentialNoiseMatrix(int width, int height, double lambda, int affectedPixelPercentage){
        int imageSize = width * height;
        int[][] noiseMatrix = new int[width][height];
        int affectedPixels = (imageSize * affectedPixelPercentage / 100);
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

    public static int[][] generateMultiplicativeRayleighNoiseMatrix(int width, int height, double phi, int affectedPixelPercentage){
        int imageSize = width * height;
        int[][] noiseMatrix = new int[width][height];
        int affectedPixels = (imageSize * affectedPixelPercentage / 100);
        int affectationCoefficient = imageSize/affectedPixels;
        int pixelCount = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (pixelCount % affectationCoefficient == 0){
                    noiseMatrix[i][j] = NumberGenerator.generateRandomRayleighNumber(phi);
                }
                else{
                    noiseMatrix[i][j] = 1;
                }
                pixelCount++;
            }
        }

        return  noiseMatrix;
    }

    public static int[][] generateAdditiveGaussianNoiseMatrix(int width, int height, double mean, double standardDev, int affectedPixelPercentage){
        int imageSize = width * height;
        int[][] noiseMatrix = new int[width][height];
        int affectedPixels = (imageSize * affectedPixelPercentage / 100);
        int affectationCoefficient = imageSize/affectedPixels;
        int pixelCount = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (pixelCount % affectationCoefficient == 0){
                    noiseMatrix[i][j] = NumberGenerator.generateRandomGaussianNumber(mean,standardDev);
                }
                else{
                    noiseMatrix[i][j] = 0;
                }
                pixelCount++;
            }
        }

        return  noiseMatrix;
    }

}
