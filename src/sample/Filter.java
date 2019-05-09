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

    public BufferedImage applyMedianFilter(BufferedImage bimg, int maskSize){
        Mask mask = new Mask(maskSize);
        mask.setMedianMask();
        BufferedImage result = applyMedianConvolution(bimg, mask);
        return result;
    }

    public BufferedImage applyWeightedMedianFilter(BufferedImage bimg){
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

    public BufferedImage enhanceEdges(BufferedImage bimg, int maskSize){
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

        horizontalMask.setPrewittMask(BorderDetectionDirection.HORIZONTAL);
        verticalMask.setPrewittMask(BorderDetectionDirection.VERTICAL);
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

    public BufferedImage applyUnidirectionalPrewitt(BufferedImage bimg, BorderDetectionDirection direction){
        Mask mask = new Mask();

        mask.setPrewittMask(direction);

        return applyUnidirectionalFilter(bimg,direction,mask);
    }

    public BufferedImage applySobel(BufferedImage bimg){
        Mask horizontalMask = new Mask();
        Mask verticalMask = new Mask();
        int[][] gradient = new int[bimg.getWidth()][bimg.getHeight()];

        horizontalMask.setSobeltMask(BorderDetectionDirection.HORIZONTAL);
        verticalMask.setSobeltMask(BorderDetectionDirection.VERTICAL);
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

    public BufferedImage applyUnidirectionalSobel(BufferedImage bimg,BorderDetectionDirection direction){
        Mask mask = new Mask();

        mask.setSobeltMask(direction);

        return applyUnidirectionalFilter(bimg,direction,mask);
    }

    public BufferedImage applyUnidirectionalUnnamed(BufferedImage bimg,BorderDetectionDirection direction){
        Mask mask = new Mask();
        mask.setUnnamedMask(direction);

        return applyUnidirectionalFilter(bimg,direction,mask);
    }

    public BufferedImage applyUnidirectionalKirsh(BufferedImage bimg, BorderDetectionDirection direction){
        Mask mask = new Mask();

        mask.setKirshtMask(direction);

        return applyUnidirectionalFilter(bimg,direction,mask);
    }

    public BufferedImage applyUnidirectionalFilter(BufferedImage bimg,BorderDetectionDirection direction, Mask mask){

        int[][] gradient = new int[bimg.getWidth()][bimg.getHeight()];

        //Calculamos las convoluciones de cada mascara
        int[][] convolutionResult = applyConvolutionReloaded(bimg,mask);


        BufferedImage result = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                int norm = (int) Math.sqrt(Math.pow(convolutionResult[i][j],2));
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
        System.out.println("*** Applying Laplace ***");
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
            int[] minMax = this.imageUtilities.findGreyMinMaxValues(zeroMatrix);
            int min = minMax[0];
            int max = minMax[1];
            System.out.println("*** Applying linear transformation ***");
            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    int p = (int) this.imageUtilities.linearTransformation(zeroMatrix[i][j],max,min);
                    result.setRGB(i,j,ColorUtilities.createRGB(p,p,p));
                }
            }
            System.out.println("*** Finished applying linear transformation ***");
            System.out.println("*** Finished applying Laplace ***");
            return result;

        }
        // Aplico y devuelvo solo el filtro de Laplace
        int[] minMax = this.imageUtilities.findGreyMinMaxValues(matrixResult);
        int min = minMax[0];
        int max = minMax[1];
        System.out.println("*** Applying linear transformation ***");
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                int p = (int) this.imageUtilities.linearTransformation(matrixResult[i][j],max,min);
                result.setRGB(i,j,ColorUtilities.createRGB(p,p,p));
            }
        }
        System.out.println("*** Finished applying linear transformation ***");
        System.out.println("*** Finished applying Laplace ***");
        return result;
    }

    public BufferedImage applyLaplaceWithSlope (BufferedImage bimg, double percent, Boolean zeroCrossing){
        System.out.println("*** Applying Laplace with Slope ***");
        BufferedImage result = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
        Mask laplaceMask = new Mask();
        laplaceMask.setLaplaceMask();
        //Aplico la mascara de Laplace con una convolucion
        int[][] matrixResult = applyConvolutionReloaded(bimg,laplaceMask);
        if(zeroCrossing){
            //Aplico metodo de Zero Crossing evaluando la Pendiente
            int [][] zeroMatrix = applyZeroCrossingEdgeDetectionWithSlope(matrixResult, percent);
            //Recorro la buffered image y la relleno con el resultado Laplace con Pendiente + Zero Crossing
            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    int p = zeroMatrix[i][j];
                    result.setRGB(i,j,ColorUtilities.createRGB(p,p,p));
                }
            }
            System.out.println("*** Finished applying Laplace with Slope***");
            return result;
        }
        // Devuelvo solo el filtro de Laplace
        int[] minMax = this.imageUtilities.findGreyMinMaxValues(matrixResult);
        int min = minMax[0];
        int max = minMax[1];
        System.out.println("*** Applying linear transformation ***");
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                int p = (int) this.imageUtilities.linearTransformation(matrixResult[i][j],max,min);
                result.setRGB(i,j,ColorUtilities.createRGB(p,p,p));
            }
        }
        System.out.println("*** Finished applying linear transformation ***");
        System.out.println("*** Finished applying Laplace with Slope***");
        return result;
    }

    public BufferedImage applyLoG(BufferedImage bimg, double sigma, Boolean zeroCrossing){
        System.out.println("*** Applying LoG ***");
        BufferedImage result = new BufferedImage(bimg.getWidth(), bimg.getHeight(), bimg.getType());
        int maskSize = (int) Math.round(6*sigma+1);
        Mask logMask = new Mask(maskSize);
        logMask.setLogMask(sigma);
        //Aplico la mascara de LoG con una convolucion
        int[][] matrixResult = applyConvolutionReloaded(bimg,logMask);
        if(zeroCrossing){
            //Aplico metodo de Zero Crossing evaluando la Pendiente, uso un porcentaje fijo, el mas bajo
            int [][] zeroMatrix = applyZeroCrossingEdgeDetectionWithSlope(matrixResult,1.0);
            //Recorro la buffered image y la relleno con el resultado Laplace + Zero Crossing
            for (int i = 0; i < bimg.getWidth(); i++) {
                for (int j = 0; j < bimg.getHeight(); j++) {
                    int p = zeroMatrix[i][j];
                    result.setRGB(i,j,ColorUtilities.createRGB(p,p,p));
                }
            }
            System.out.println("*** Finished applying Log***");
            return this.imageUtilities.imageNegative(result);
        }
        // Si ZC esta en false devuelvo solo el filtro de Laplace
        int[] minMax = this.imageUtilities.findGreyMinMaxValues(matrixResult);
        int min = minMax[0];
        int max = minMax[1];
        System.out.println("*** Applying linear transformation ***");
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                int p = (int) this.imageUtilities.linearTransformation(matrixResult[i][j],max,min);
                result.setRGB(i,j,ColorUtilities.createRGB(p,p,p));
            }
        }
        System.out.println("*** Finished applying linear transformation ***");
        System.out.println("*** Finished applying Log ***");
        return result;
    }

    public BufferedImage applyIsotropicDifusion(BufferedImage bimg, int iterations){

        BufferedImage result = new BufferedImage(bimg.getWidth(),bimg.getHeight(),bimg.getType());
        int counter = 0;
        if (imageUtilities.isGreyImage(bimg)){
            Image image = new Image(bimg);
            image.convertToGreyDataMatrix();
            int [][] greyDataMatrix = image.getGreyDataMatrix();
            while(counter < iterations){
                greyDataMatrix = applyFirstDerivate(greyDataMatrix);
                counter ++;
            }
            int[] minMax = this.imageUtilities.findGreyMinMaxValues(greyDataMatrix);
            int min = minMax[0];
            int max = minMax[1];
            System.out.println("*** Applying linear transformation ***");
            for (int i = 0; i < greyDataMatrix.length; i++) {
                for (int j = 0; j < greyDataMatrix[0].length; j++) {
                    int p = (int) this.imageUtilities.linearTransformation(greyDataMatrix[i][j],max,min);
                    result.setRGB(i,j,ColorUtilities.createRGB(p,p,p));
                }
            }
            System.out.println("*** Finished applying linear transformation ***");
        }
        else{
           Image image = new Image(bimg);
           int [][] redChannelMatrix = image.getRedDataMatrixChannel();
           int [][] greenChannelMatrix = image.getGreenDataMatrixChannel();
           int [][] blueChannelMatrix = image.getBlueDataMatrixChannel();
            while(counter < iterations){
                redChannelMatrix = applyFirstDerivate(redChannelMatrix);
                greenChannelMatrix = applyFirstDerivate(greenChannelMatrix);
                blueChannelMatrix = applyFirstDerivate(blueChannelMatrix);
                counter ++;
            }
            int[] redMinMax = this.imageUtilities.findGreyMinMaxValues(redChannelMatrix);
            int[] greenMinMax = this.imageUtilities.findGreyMinMaxValues(greenChannelMatrix);
            int[] blueMinMax = this.imageUtilities.findGreyMinMaxValues(blueChannelMatrix);
            int redMin = redMinMax[0];
            int redMax = redMinMax[1];
            int greenMin = greenMinMax[0];
            int greenMax = greenMinMax[1];
            int blueMin = blueMinMax[0];
            int blueMax = blueMinMax[1];
            System.out.println("*** Applying linear transformation ***");
            for (int i = 0; i < result.getWidth(); i++) {
                for (int j = 0; j < result.getHeight(); j++) {
                    int r = (int) this.imageUtilities.linearTransformation(redChannelMatrix[i][j],redMax,redMin);
                    int g = (int) this.imageUtilities.linearTransformation(greenChannelMatrix[i][j],greenMax,greenMin);
                    int b = (int) this.imageUtilities.linearTransformation(blueChannelMatrix[i][j],blueMax,blueMin);
                    result.setRGB(i,j,ColorUtilities.createRGB(r,g,b));
                }
            }
            System.out.println("*** Finished applying linear transformation ***");
        }

        return result;
    }

    public int [][] applyFirstDerivate(int [][] channelMatrix){
        int[][] matrixResult = new int[channelMatrix.length][channelMatrix[0].length];
        double P;
        double Dn=0,Ds=0,De=0,Do=0;
        //Recorro la matriz del canal
        for (int i = 0; i < channelMatrix.length; i++) {
            for (int j = 0; j < channelMatrix[0].length; j++) {
                P = (double) channelMatrix[i][j];
                if(i==0){
                    //Esquina sup izq
                    if(j==0){
                        Dn = 0;
                        Ds = (double) channelMatrix[i][j+1] - (double) channelMatrix[i][j];
                        De = (double) channelMatrix[i+1][j] - (double) channelMatrix[i][j];
                        Do = 0;
                    }
                    //Esquina inf izq
                    else if(j==channelMatrix[0].length-1){
                        Dn = (double) channelMatrix[i][j-1] - (double) channelMatrix[i][j];
                        Ds = 0;
                        De = (double) channelMatrix[i+1][j] - (double) channelMatrix[i][j];
                        Do = 0;
                    }
                    //Borde izq
                    else {
                        Dn = (double) channelMatrix[i][j-1] - (double) channelMatrix[i][j];
                        Ds = (double) channelMatrix[i][j+1] - (double) channelMatrix[i][j];
                        De = (double) channelMatrix[i+1][j] - (double) channelMatrix[i][j];
                        Do = 0;
                    }
                }
                else if(i==channelMatrix.length-1) {
                    //Esquina sup dcha
                    if (j == 0) {
                        Dn = 0;
                        Ds = (double) channelMatrix[i][j + 1] - (double) channelMatrix[i][j];
                        De = 0;
                        Do = (double) channelMatrix[i - 1][j] - (double) channelMatrix[i][j];
                    }
                    //Esquina inf dcha
                    else if(j==channelMatrix[0].length-1){
                        Dn = (double) channelMatrix[i][j-1] - (double) channelMatrix[i][j];
                        Ds = 0;
                        De = 0;
                        Do = (double) channelMatrix[i-1][j] - (double) channelMatrix[i][j];
                    }
                    //Borde dcho
                    else {
                        Dn = (double) channelMatrix[i][j-1] - (double) channelMatrix[i][j];
                        Ds = (double) channelMatrix[i][j+1] - (double) channelMatrix[i][j];
                        De = 0;
                        Do = (double) channelMatrix[i-1][j] - (double) channelMatrix[i][j];
                    }
                }
                else{
                    //Borde superior
                    if(i!=0 && j==0){
                        Dn = 0;
                        Ds = (double) channelMatrix[i][j + 1] - (double) channelMatrix[i][j];
                        De = (double) channelMatrix[i + 1][j] - (double) channelMatrix[i][j];
                        Do = (double) channelMatrix[i - 1][j] - (double) channelMatrix[i][j];
                    }
                    //Borde inferior
                    else if(i!=0 && j==channelMatrix[0].length-1){
                        Dn = (double) channelMatrix[i][j - 1] - (double) channelMatrix[i][j];
                        Ds = 0;
                        De = (double) channelMatrix[i + 1][j] - (double) channelMatrix[i][j];
                        Do = (double) channelMatrix[i - 1][j] - (double) channelMatrix[i][j];
                    }
                    //Imagen sin bordes
                    else if (i!=0 && j!=0){
                        Dn = (double) channelMatrix[i][j - 1] - (double) channelMatrix[i][j];
                        Ds = (double) channelMatrix[i][j + 1] - (double) channelMatrix[i][j];
                        De = (double) channelMatrix[i + 1][j] - (double) channelMatrix[i][j];
                        Do = (double) channelMatrix[i - 1][j] - (double) channelMatrix[i][j];
                    }
                }
                P = P + (Dn + Ds + De + Do)/4.0;
                matrixResult[i][j] = (int) Math.round(P);
            }
        }
        return matrixResult;
    }

    public int[][] applyZeroCrossingEdgeDetection(int[][] imageMatrix){
        System.out.println("*** Applying ZC ***");
        int[][] resultMatrix = new int[imageMatrix.length][imageMatrix[0].length];
        //Veo cruces por cero a lo largo de cada fila recorriendo toda la matriz y analizando horizontalmente
        for (int i = 0; i < imageMatrix.length-1; i++) {
            for (int j = 0; j < imageMatrix[0].length; j++) {
                int currentPixel = imageMatrix[i][j];
                int rightPixel = imageMatrix[i+1][j];
                //Comparo el pixel actual el de su dcha, si hay un cambio de signo pongo un blanco
                if ( (currentPixel > 0 && rightPixel < 0) || (currentPixel < 0 && rightPixel > 0)){
                    resultMatrix[i][j] = 255;
                }
                //Tengo que ver que hay a mis costados (teniendo cuidado de que i NO sea el elemento 0 del array)
                else if(currentPixel == 0 && i > 0){
                    int leftPixel = imageMatrix[i -1][j];
                    if((leftPixel > 0 && rightPixel < 0) || (leftPixel < 0 && rightPixel > 0)){
                        resultMatrix[i][j] = 255;
                    }
                }
            }
        }

        //Veo cruces por cero a lo largo de cada columna recorriendo toda la matriz y analizando verticalmente
        for (int i = 0; i < imageMatrix.length; i++) {
            for (int j = 0; j < imageMatrix[0].length-1; j++) {
                int currentPixel = imageMatrix[i][j];
                int downPixel = imageMatrix[i][j+1];
                //Comparo el pixel actual y el de abajo, si hay un cambio de signo pongo un blanco
                if ( (currentPixel > 0 && downPixel < 0) || (currentPixel < 0 && downPixel > 0)){
                    resultMatrix[i][j] = 255;
                }
                //Si el pixel actual es cero, comparo el pixel de arriba y abajo (teniendo cuidado de que i NO sea el elemento 0 del array)
                else if(currentPixel == 0 && j > 0){
                    int upPixel = imageMatrix[i][j-1];
                    if((upPixel > 0 && downPixel < 0) || (upPixel < 0 && downPixel > 0)){
                        resultMatrix[i][j] = 255;
                    }
                }
            }
        }

        System.out.println("*** Finished applying ZC ***");
        return resultMatrix;
    }

    public int[][] applyZeroCrossingEdgeDetectionWithSlope(int[][] imageMatrix, double percent){
        System.out.println("*** Applying ZC with slope ***");
        int[][] resultMatrix = new int[imageMatrix.length][imageMatrix[0].length];
        //Calculo la pendiente maxima
        int max = getMaxSlope(imageMatrix);
        //Defino un umbral usando un porcentaje de la pendiente maxima
        double treshold = max * (percent/100.0);
        System.out.println("Umbral: " + treshold );
        //Veo cruces por cero a lo largo de cada fila recorriendo toda la matriz y analizando horizontalmente
        for (int i = 0; i < imageMatrix.length-1; i++) {
            for (int j = 0; j < imageMatrix[0].length; j++) {
                int currentPixel = imageMatrix[i][j];
                int rightPixel = imageMatrix[i+1][j];
                //Comparo el pixel con el de su dcha, si hay un cambio de signo calculo la pendiente entre ambos |a|+|b|
                if ( (currentPixel > 0 && rightPixel < 0) || (currentPixel < 0 && rightPixel > 0)){
                    //Calculo la pendiente del current y el dcho y umbralizo
                    if(calculateSlopeAbs(currentPixel,rightPixel) >= treshold){
                        resultMatrix[i][j] = 255;
                    }
                }
                //Tengo que ver que hay a mis costados (teniendo cuidado de que i NO sea el elemento 0 del array)
                else if(currentPixel == 0 && i > 0){
                    int leftPixel = imageMatrix[i -1][j];
                    if((leftPixel > 0 && rightPixel < 0) || (leftPixel < 0 && rightPixel > 0)){
                        //Calculo la pendiente del izq y el dcho y umbralizo
                        if(calculateSlopeAbs(leftPixel,rightPixel) >= treshold){
                            resultMatrix[i][j] = 255;
                        }
                    }
                }
            }
        }

        //Veo cruces por cero a lo largo de cada columna recorriendo toda la matriz y analizando verticalmente
        for (int i = 0; i < imageMatrix.length; i++) {
            for (int j = 0; j < imageMatrix[0].length-1; j++) {
                int currentPixel = imageMatrix[i][j];
                int downPixel = imageMatrix[i][j+1];

                if ( (currentPixel > 0 && downPixel < 0) || (currentPixel < 0 && downPixel > 0)){
                    if(calculateSlopeAbs(currentPixel,downPixel) >= treshold){
                        resultMatrix[i][j] = 255;
                    }
                }

                else if(currentPixel == 0 && j > 0){
                    int upPixel = imageMatrix[i][j-1];
                    if((upPixel > 0 && downPixel < 0) || (upPixel < 0 && downPixel > 0)){
                        if(calculateSlopeAbs(upPixel,downPixel) >= treshold){
                            resultMatrix[i][j] = 255;
                        }
                    }
                }
            }
        }

        System.out.println("*** Finished applying ZC with slope ***");
        return resultMatrix;
    }

    private int calculateSlopeAbs(int a, int b){
        int result = (Math.abs(a) + Math.abs(b));
        return result;
    }

    private int getMaxSlope(int[][] imageMatrix){
        int [][] slopeMatrix = new int[imageMatrix.length][imageMatrix[0].length];
        for (int i = 0; i < imageMatrix.length-1; i++) {
            for (int j = 0; j < imageMatrix[0].length; j++) {
                int currentPixel = imageMatrix[i][j];
                int rightPixel = imageMatrix[i+1][j];
                //Comparo el pixel a izq y a dcha, si hay un cambio de signo calculo la pendiente entre ambos |a+b|
                if ( (currentPixel > 0 && rightPixel < 0) || (currentPixel < 0 && rightPixel > 0)){
                        slopeMatrix[i][j] = calculateSlopeAbs(currentPixel,rightPixel);
                }
                //Tengo que ver que hay a mis costados (teniendo cuidado de que i NO sea el elemento 0 del array)
                else if(currentPixel == 0 && i > 0){
                    int leftPixel = imageMatrix[i -1][j];
                    if((leftPixel > 0 && rightPixel < 0) || (leftPixel < 0 && rightPixel > 0)){
                        //Calculo la pendiente del izq y el dcho y umbralizo
                        slopeMatrix[i][j] = calculateSlopeAbs(currentPixel,rightPixel);
                    }
                }
            }
        }
        int max = 0;
        for (int i = 0; i < slopeMatrix.length; i++) {
            for (int j = 0; j < slopeMatrix[0].length; j++) {
                if (slopeMatrix[i][j]>max){
                    max = slopeMatrix[i][j];
                }
            }
        }
        System.out.println("Pendiente maxima: " + max);
        return max;
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
        System.out.println("*** Applying Convolution Reloaded ***");
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
                        grey += Math.round(greyDataMatrix[i+k][j+l] * mask.getValue(k,l));
                    }
                }
                result[i + mask.getCenter()][j + mask.getCenter()] = grey;
                //System.out.println("r: " +grey);
                grey = 0;

            }
        }
        System.out.println("*** Finished applying Convolution Reloaded ***");
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
