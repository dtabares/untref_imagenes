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

    public int[][] applyRawGaussFilter(BufferedImage bimg, double sigma){
        int maskSize = (int) Math.round(2*sigma+1);
        Mask mask = new Mask(maskSize);
        mask.setGaussMaskRevised(sigma);
        int[][] result = applyConvolutionReloaded(bimg, mask);
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

    public int[][] applyUnidirectionalRawSobel(int[][] image,BorderDetectionDirection direction, int width, int height){
        Mask mask = new Mask();

        mask.setSobeltMask(direction);

        return applyRawUnidirectionalFilter(image,direction,mask,width,height);
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

    public int[][] applyRawUnidirectionalFilter(int[][] image,BorderDetectionDirection direction, Mask mask,int width, int height){

        int[][] gradient = new int[width][height];

        //Calculamos las convoluciones de cada mascara
        int[][] convolutionResult = applyRawConvolutionReloaded(image,mask,width,height);
        //return convolutionResult;


        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int norm = (int) Math.sqrt(Math.pow(convolutionResult[i][j],2));
                gradient[i][j] = norm;
            }
        }
        return gradient;
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
                greyDataMatrix = applyFirstDerivate(greyDataMatrix,false, 0.0);
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
                redChannelMatrix = applyFirstDerivate(redChannelMatrix,false, 0.0);
                greenChannelMatrix = applyFirstDerivate(greenChannelMatrix,false, 0.0);
                blueChannelMatrix = applyFirstDerivate(blueChannelMatrix,false, 0.0);
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

    public BufferedImage applyAnisotropicDifusion(BufferedImage bimg, int iterations, double sigma){

        BufferedImage result = new BufferedImage(bimg.getWidth(),bimg.getHeight(),bimg.getType());
        int counter = 0;
        if (imageUtilities.isGreyImage(bimg)){
            Image image = new Image(bimg);
            image.convertToGreyDataMatrix();
            int [][] greyDataMatrix = image.getGreyDataMatrix();
            while(counter < iterations){
                greyDataMatrix = applyFirstDerivate(greyDataMatrix,true,sigma);
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
                redChannelMatrix = applyFirstDerivate(redChannelMatrix,true, sigma);
                greenChannelMatrix = applyFirstDerivate(greenChannelMatrix,true, sigma);
                blueChannelMatrix = applyFirstDerivate(blueChannelMatrix,true, sigma);
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

    public BufferedImage applyBilateralFilter(BufferedImage bimg, double sigma_r, double sigma_s){

        double maxSigma = Math.max(sigma_r,sigma_s);
        int maskSize = (int) Math.round(2 *  maxSigma + 1);
        Mask bilateralMask = new Mask(11);
        Image image = new Image(bimg);
        int[][] redChannel = image.getRedDataMatrixChannel();
        int[][] greenChannel = image.getGreenDataMatrixChannel();
        int[][] blueChannel = image.getBlueDataMatrixChannel();

        int[][] filteredRedChannel = this.applyBilateralFilterToChannel(redChannel,sigma_r,sigma_s,bilateralMask, image.getWidth(),image.getHeight());
        int[][] filteredGreenChannel = this.applyBilateralFilterToChannel(greenChannel,sigma_r,sigma_s,bilateralMask, image.getWidth(),image.getHeight());
        int[][] filteredBlueChannel = this.applyBilateralFilterToChannel(blueChannel,sigma_r,sigma_s,bilateralMask, image.getWidth(),image.getHeight());

        BufferedImage result = imageUtilities.copyImageIntoAnother(bimg);

        // Relleno la imagen a devolver
        for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {
                result.setRGB(i,j,ColorUtilities.createRGB(filteredRedChannel[i][j], filteredGreenChannel[i][j], filteredBlueChannel[i][j]));
            }
        }
        return result;
    }

    public BufferedImage applyCanny(BufferedImage bimg, double sigma, int t1, int t2){
        BufferedImage result = new BufferedImage(bimg.getWidth(),bimg.getHeight(),BufferedImage.TYPE_BYTE_GRAY);

        //Llamo a apply Canny que me devuelve un int[][]
        int[][] temp = this.applyRawCanny(bimg,sigma,t1,t2);

        //Transformo eso a buffered image
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                int p = ColorUtilities.createRGB(temp[i][j],temp[i][j],temp[i][j]);
                result.setRGB(i,j,p);
            }
        }


        return result;

    }

    public int[][] applyRawCanny(BufferedImage bimg, double sigma, int t1, int t2){
        //int[][] result = new int[bimg.getWidth()][bimg.getHeight()];
        int[][] angles = new int[bimg.getWidth()][bimg.getHeight()];
        int[][] gradient = new int[bimg.getWidth()][bimg.getHeight()];
        int[][] gaussFilteredImage;
        if(sigma > 0){
            //Aplico el Filtro Gaussiano
            gaussFilteredImage = this.applyRawGaussFilter(bimg,sigma);
        }
        else{
            Image im = new Image(bimg);
            im.convertToGreyDataMatrix();
            gaussFilteredImage = im.getGreyDataMatrix();
        }


        int[] minMax = this.imageUtilities.findGreyMinMaxValues(gaussFilteredImage);

        //Aplico Sobel y obtengo Gx (horizontal) y Gy (vertical)
        int[][] gx = this.applyUnidirectionalRawSobel(gaussFilteredImage,BorderDetectionDirection.HORIZONTAL,bimg.getWidth(),bimg.getHeight());
        int[][] gy = this.applyUnidirectionalRawSobel(gaussFilteredImage,BorderDetectionDirection.VERTICAL,bimg.getWidth(),bimg.getHeight());

        // Calculo G como la suma de los modulos de Gx y Gy
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                gradient[i][j] = (int) Math.sqrt(Math.pow(gx[i][j],2) + Math.pow(gy[i][j],2));
            }
        }

        //pruebo de hacer una TL antes de seguir
        minMax = this.imageUtilities.findGreyMinMaxValues(gradient);
        int min = minMax[0];
        int max = minMax[1];

        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                gradient[i][j] = (int) this.imageUtilities.linearTransformation(gradient[i][j],max,min);
            }
        }


        //Voy calculando el angulo y me guardo en la matriz de angulos la direccion del borde
        double angle;
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                if(gx[i][j] == 0){
                    angle = 0;
                }
                else{
                    angle = Math.toDegrees(Math.atan2(gy[i][j], gx[i][j])) + 90;
                }
                //System.out.println(angle);
                angles[i][j] = this.classifyAngleForCanny(angle);
                //System.out.println(angles[i][j]);
            }
        }
        //Aplico supresion de no maximos
        int neighborA = 0;
        int neighborB = 0;
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                if(gradient[i][j] != 0){
                    switch (angles[i][j]){
                        case 0:
                            //West
                            if(i - 1 >= 0){
                                neighborA = gradient[i-1][j];
                            }
                            else {
                                neighborA = 0;
                            }
                            //East
                            if(i + 1 <= bimg.getWidth()){
                                neighborB = gradient[i+1][j];
                            }
                            else {
                                neighborB = 0;
                            }
                            break;
                        case 45:
                            //NE
                            if(i + 1 <= bimg.getWidth() && j -1 >= 0){
                                neighborB = gradient[i+1][j-1];
                            }
                            else {
                                neighborB = 0;
                            }
                            //SW
                            if(i - 1 >= 0 && j + 1 <= bimg.getHeight()){
                                neighborA = gradient[i-1][j+1];
                            }
                            else {
                                neighborA = 0;
                            }
                            break;
                        case 90:
                            //North
                            if(j - 1 >= 0){
                                neighborA = gradient[i][j-1];
                            }
                            else {
                                neighborA = 0;
                            }
                            //South
                            if(j + 1 <= bimg.getHeight()){
                                neighborB = gradient[i][j+1];
                            }
                            else {
                                neighborB = 0;
                            }
                            break;
                        case 135:
                            //North West
                            if(i - 1 >= 0 && j -1 >= 0){
                                neighborA = gradient[i-1][j-1];
                            }
                            else {
                                neighborA = 0;
                            }
                            //South East
                            if(i + 1 <= bimg.getWidth() && j + 1 <= bimg.getHeight()){
                                neighborB = gradient[i+1][j+1];
                            }
                            else {
                                neighborB = 0;
                            }
                            break;
                    }

                    //Hago la comparacion, si alguno de sus vecinos es mayor, lo borro como borde
                    if(neighborA > gradient[i][j] || neighborB > gradient[i][j]){
                        gradient[i][j] = 0;
                    }
                }
            }
        }
        //Aplico Umbralizacion por histeresis
        //Primer pasada
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                if(gradient[i][j] >= t2){
                    gradient[i][j] = 255;
                }
                else if(gradient[i][j] <= t1){
                    gradient[i][j] = 0;
                }
            }
        }

        //Segunda pasada
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                if(gradient[i][j] > t1 && gradient[i][j] < t2){
                    if (anyNeighborIsBorder(gradient, i, j, bimg.getWidth(), bimg.getHeight())){
                        gradient[i][j] = 255;
                    }
                    else{
                        gradient[i][j] = 0;
                    }
                }

                if(gradient[i][j] != 0 && gradient[i][j] != 255)
                {
                    System.out.println( i +" " +j +" "+ gradient[i][j]);
                }

            }
        }

        return gradient;
        //return gaussFilteredImage;

    }

    private boolean anyNeighborIsBorder(int[][] image, int i, int j, int width, int height){
        boolean borderFound = false;
        boolean hasNorth = false;
        boolean hasSouth = false;
        boolean hasEast = false;
        boolean hasWest = false;

        if(j -1 >= 0){
            hasNorth = true;
        }
        if(j + 1 <= height){
            hasSouth = true;
        }
        if(i + 1 <= width){
            hasEast = true;
        }
        if(i -1 >= 0){
            hasWest = true;
        }

        if(hasWest){
            //W
            if(image[i-1][j] == 255){
                return true;
            }
        }
        if(hasEast){
            //E
            if(image[i+1][j] == 255){
                return true;
            }
        }

        if(hasNorth){
            //N
            if(image[i][j-1] == 255){
                return true;
            }
            if(hasWest){
                //NW
                if(image[i-1][j-1] == 255){
                    return true;
                }
            }
            if(hasEast){
                //NE
                if(image[i+1][j-1] == 255){
                    return true;
                }
            }
        }

        if(hasSouth){
            //S
            if(image[i][j+1] == 255){
                return true;
            }
            if(hasWest){
                //SW
                if(image[i-1][j+1] == 255){
                    return true;
                }
            }
            if(hasEast){
                //SE
                if(image[i+1][j+1] == 255){
                    return true;
                }
            }
        }

        return borderFound;
    }

    public BufferedImage applySusan(BufferedImage bimg, int selection){
        BufferedImage result = this.imageUtilities.copyImageIntoAnother(bimg, BufferedImage.TYPE_INT_RGB);
        int cornerColor = ColorUtilities.createRGB(255,0,0);
        int borderColor = ColorUtilities.createRGB(0,255,0);
        int color;
        if(selection == 0){
            color = borderColor;
        }
        else{
            color = cornerColor;
        }

        //Llamo a apply Susan que me devuelve un int[][]
        int[][] temp = this.applyRawSusan(bimg, selection);

        //Transformo eso a buffered image
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                if(temp[i][j] == 1){
                    result.setRGB(i,j,color);
                }
            }
        }


        return result;
    }

    private int[][] applyRawSusan(BufferedImage bimg, int selection){
        int maskSize = 7;
        int[][] cornersMatrix = new int[bimg.getWidth()][bimg.getHeight()];
        int[][] borderMatrix = new int[bimg.getWidth()][bimg.getHeight()];
        int center = 3;
        Image im = new Image(bimg);
        im.convertToGreyDataMatrix();
        int[][] greyImage = im.getGreyDataMatrix();
        int widthLimit = bimg.getWidth() - maskSize;
        int heightLimit = bimg.getHeight() - maskSize;
        int r0;
        int r;
        int threshold = 27;
        double sr0;
        int nr0;

        //recorro la imagen
        for (int i = 0; i < widthLimit; i++) {
            for (int j = 0; j < heightLimit; j++) {
                // tomo el pixel central
                r0 = greyImage[i+center][j+center];
                nr0 = 0;
                // creo un array donde voy a poner el resultado de c(r,r0)
                int[] nr0Array = new int[37];
                int nr0Index = 0;
                //Voy pasando la mascara verticalmente
                for (int k = j; k < j+maskSize; k++) {
                    //Dependiendo de la altura de la mascara, se como me tengo que mover horizontalmente
                    //ya que la mascara es circular
                    if (k == j || k == j+6){
                        //eval 3
                        for (int l = (i + center - 1); l <= (i + center + 1); l++) {
                            r = greyImage[l][k];
                            if (Math.abs(r - r0) < threshold){
                                nr0Array[nr0Index] = 1;
                            }
                            else{
                                nr0Array[nr0Index] = 0;
                            }
                            nr0Index++;
                        }

                    }
                    else if(k == j+1 || k == j+5){
                        // eval 5
                        for (int l = (i + center - 2); l <= (i + center + 2); l++) {
                            r = greyImage[l][k];
                            if (Math.abs(r - r0) < threshold){
                                nr0Array[nr0Index] = 1;
                            }
                            else{
                                nr0Array[nr0Index] = 0;
                            }
                            nr0Index++;
                        }
                    }
                    else{
                        // eval 7
                        for (int l = i; l < (i+7); l++) {
                            r = greyImage[l][k];
                            if (Math.abs(r - r0) < threshold){
                                nr0Array[nr0Index] = 1;
                            }
                            else{
                                nr0Array[nr0Index] = 0;
                            }
                            nr0Index++;
                        }
                    }
                }

                //Termine de comparar todas las intensidades y nr0Array esta lleno
                //Debo calcular nr0 como la sumatoria de sus elementos
                for (int k = 0; k < nr0Array.length; k++) {
                    nr0 = nr0 + nr0Array[k];
                }
                sr0 = 1.0 - (nr0/37.0);

                if(sr0 >= 0.40 && sr0 < 0.55){
                    //border
                    borderMatrix[i+center][j+center] = 1;
                }
                else if(sr0 >= 0.60){
                    //corner
                    cornersMatrix[i+center][j+center] = 1;
                }


            }
        }

        if (selection == 0){
            return borderMatrix;
        }
        return cornersMatrix;
    }

    private int[][] applyBilateralFilterToChannel(int[][] channelData, double sigma_r, double sigma_s, Mask mask, int imageWidth, int imageHeight){
        int[][] filteredChannel = this.imageUtilities.cloneChannelData(channelData);
        int widthLimit = imageWidth - mask.getSize();
        int heightLimit = imageHeight - mask.getSize();
        int value = 0;
        double temp;
        for (int i = 0; i <= widthLimit; i++) {
            for (int j = 0; j <= heightLimit; j++) {
                //Genero máscara
                mask.setBilateralMask(channelData,i + mask.getCenter(),j + + mask.getCenter(),sigma_r,sigma_s);
                //Convoluciono
                for (int k = 0; k < mask.getSize(); k++) {
                    for (int l = 0; l < mask.getSize(); l++) {
                        value += Math.round(channelData[i+k][j+l] * mask.getValue(k,l));
                    }
                }
                //Fin Convolucion
                temp = value / mask.getSum();
                if(temp> 255 || temp < 0){
                    System.out.println("Value out of range:" + temp);
                }
                filteredChannel[i + mask.getCenter()][j + mask.getCenter()] = (int)temp;
                value = 0;
            }
        }


        return filteredChannel;
    }

    public int [][] applyFirstDerivate(int [][] channelMatrix, boolean anisotropic, double sigma){
        int[][] matrixResult = new int[channelMatrix.length][channelMatrix[0].length];
        double P;
        double Dn=0,Ds=0,De=0,Do=0;
        double Cn=1,Cs=1,Ce=1,Co=1;
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
                        if(anisotropic){
                            Cn = lorentzGradient(sigma,Dn);
                            Cs = lorentzGradient(sigma,Ds);
                            Ce = lorentzGradient(sigma,De);
                            Co = lorentzGradient(sigma,Do);
                        }
                    }
                    //Esquina inf izq
                    else if(j==channelMatrix[0].length-1){
                        Dn = (double) channelMatrix[i][j-1] - (double) channelMatrix[i][j];
                        Ds = 0;
                        De = (double) channelMatrix[i+1][j] - (double) channelMatrix[i][j];
                        Do = 0;
                        if(anisotropic){
                            Cn = lorentzGradient(sigma,Dn);
                            Cs = lorentzGradient(sigma,Ds);
                            Ce = lorentzGradient(sigma,De);
                            Co = lorentzGradient(sigma,Do);
                        }
                    }
                    //Borde izq
                    else {
                        Dn = (double) channelMatrix[i][j-1] - (double) channelMatrix[i][j];
                        Ds = (double) channelMatrix[i][j+1] - (double) channelMatrix[i][j];
                        De = (double) channelMatrix[i+1][j] - (double) channelMatrix[i][j];
                        Do = 0;
                        if(anisotropic){
                            Cn = lorentzGradient(sigma,Dn);
                            Cs = lorentzGradient(sigma,Ds);
                            Ce = lorentzGradient(sigma,De);
                            Co = lorentzGradient(sigma,Do);
                        }
                    }
                }
                else if(i==channelMatrix.length-1) {
                    //Esquina sup dcha
                    if (j == 0) {
                        Dn = 0;
                        Ds = (double) channelMatrix[i][j + 1] - (double) channelMatrix[i][j];
                        De = 0;
                        Do = (double) channelMatrix[i - 1][j] - (double) channelMatrix[i][j];
                        if(anisotropic){
                            Cn = lorentzGradient(sigma,Dn);
                            Cs = lorentzGradient(sigma,Ds);
                            Ce = lorentzGradient(sigma,De);
                            Co = lorentzGradient(sigma,Do);
                        }
                    }
                    //Esquina inf dcha
                    else if(j==channelMatrix[0].length-1){
                        Dn = (double) channelMatrix[i][j-1] - (double) channelMatrix[i][j];
                        Ds = 0;
                        De = 0;
                        Do = (double) channelMatrix[i-1][j] - (double) channelMatrix[i][j];
                        if(anisotropic){
                            Cn = lorentzGradient(sigma,Dn);
                            Cs = lorentzGradient(sigma,Ds);
                            Ce = lorentzGradient(sigma,De);
                            Co = lorentzGradient(sigma,Do);
                        }
                    }
                    //Borde dcho
                    else {
                        Dn = (double) channelMatrix[i][j-1] - (double) channelMatrix[i][j];
                        Ds = (double) channelMatrix[i][j+1] - (double) channelMatrix[i][j];
                        De = 0;
                        Do = (double) channelMatrix[i-1][j] - (double) channelMatrix[i][j];
                        if(anisotropic){
                            Cn = lorentzGradient(sigma,Dn);
                            Cs = lorentzGradient(sigma,Ds);
                            Ce = lorentzGradient(sigma,De);
                            Co = lorentzGradient(sigma,Do);
                        }
                    }
                }
                else{
                    //Borde superior
                    if(i!=0 && j==0){
                        Dn = 0;
                        Ds = (double) channelMatrix[i][j + 1] - (double) channelMatrix[i][j];
                        De = (double) channelMatrix[i + 1][j] - (double) channelMatrix[i][j];
                        Do = (double) channelMatrix[i - 1][j] - (double) channelMatrix[i][j];
                        if(anisotropic){
                            Cn = lorentzGradient(sigma,Dn);
                            Cs = lorentzGradient(sigma,Ds);
                            Ce = lorentzGradient(sigma,De);
                            Co = lorentzGradient(sigma,Do);
                        }
                    }
                    //Borde inferior
                    else if(i!=0 && j==channelMatrix[0].length-1){
                        Dn = (double) channelMatrix[i][j - 1] - (double) channelMatrix[i][j];
                        Ds = 0;
                        De = (double) channelMatrix[i + 1][j] - (double) channelMatrix[i][j];
                        Do = (double) channelMatrix[i - 1][j] - (double) channelMatrix[i][j];
                        if(anisotropic){
                            Cn = lorentzGradient(sigma,Dn);
                            Cs = lorentzGradient(sigma,Ds);
                            Ce = lorentzGradient(sigma,De);
                            Co = lorentzGradient(sigma,Do);
                        }
                    }
                    //Imagen sin bordes
                    else if (i!=0 && j!=0){
                        Dn = (double) channelMatrix[i][j - 1] - (double) channelMatrix[i][j];
                        Ds = (double) channelMatrix[i][j + 1] - (double) channelMatrix[i][j];
                        De = (double) channelMatrix[i + 1][j] - (double) channelMatrix[i][j];
                        Do = (double) channelMatrix[i - 1][j] - (double) channelMatrix[i][j];
                        if(anisotropic){
                            Cn = lorentzGradient(sigma,Dn);
                            Cs = lorentzGradient(sigma,Ds);
                            Ce = lorentzGradient(sigma,De);
                            Co = lorentzGradient(sigma,Do);
                        }
                    }
                }
                //System.out.print("I = " + P + " + ((" + Cn + " * " + Dn + " + " + Cs + " * " + Ds + " + " + Ce + " * " + De + " + " + Co + " * " + Do + " + " + ") * 0.25)");
                P = P + ((Cn*Dn) + (Cs*Ds) + (Ce*De) + (Co*Do))/4.0;
//                System.out.print(" = " + P);
//                System.out.println(" | I Casteado: " + (int) Math.round(P));
                matrixResult[i][j] = (int) Math.ceil(P);
            }
        }
        return matrixResult;
    }

    public double lorentzGradient(double sigma, double derivate){
        //System.out.println((double) ( 1/ ( ((double) (Math.pow(Math.abs(derivate), 2) / Math.pow(sigma, 2))) + 1) ));
        return (double) ( 1/ ( ((double) (Math.pow(Math.abs(derivate), 2) / Math.pow(sigma, 2))) + 1) );
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
        //int result = (Math.abs(a+b));
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
                result[i][j] = greyDataMatrix[i][j];
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

    private int[][] applyRawConvolutionReloaded(int[][] image, Mask mask, int width, int height){
        int grey;
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                result[i][j] = 0;
            }
        }
        int widthLimit = width - mask.getSize();
        int heightLimit = height - mask.getSize();
        grey = 0;

        for (int i = 0; i <= widthLimit; i++) {
            for (int j = 0; j <= heightLimit; j++) {
                for (int k = 0; k < mask.getSize(); k++) {
                    for (int l = 0; l < mask.getSize(); l++) {
                        grey += Math.round(image[i+k][j+l] * mask.getValue(k,l));
                    }
                }
                result[i + mask.getCenter()][j + mask.getCenter()] = grey;
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

    private int classifyAngleForCanny(double angle){
        int newAngle = 0;


        if(angle >= 22.5 && angle < 67.5){
            newAngle = 45;
        }
        else if(angle >= 67.5 && angle < 112.5){
            newAngle = 90;
        }
        else if(angle >= 112.5 && angle < 157.5){
            newAngle = 135;
        }


        return newAngle;
    }

    public BufferedImage applyHarris(BufferedImage bimg, int sigma, int maskSize, double percent){
        int[][] resultMatrix = new int[bimg.getWidth()][bimg.getHeight()];
        Image image = new Image(bimg);
        image.convertToGreyDataMatrix();

        // Paso a escala de grises
        int[][] greyDataMatrix = image.getGreyDataMatrix();

        //Paso 1
        // Aplico Sobel y obtengo Gx (horizontal) y Gy (vertical)
        int[][] gx = this.applyUnidirectionalRawSobel(greyDataMatrix,BorderDetectionDirection.HORIZONTAL,bimg.getWidth(),bimg.getHeight());
        int[][] gy = this.applyUnidirectionalRawSobel(greyDataMatrix,BorderDetectionDirection.VERTICAL,bimg.getWidth(),bimg.getHeight());
        int[][] g45 = this.applyUnidirectionalRawSobel(greyDataMatrix,BorderDetectionDirection.DIAGONAL45,bimg.getWidth(),bimg.getHeight());
        int[][] g135 = this.applyUnidirectionalRawSobel(greyDataMatrix,BorderDetectionDirection.DIAGONAL135,bimg.getWidth(),bimg.getHeight());

        //Paso 2
        // Elevo  matrices al cuadrado
        for (int i = 0; i < gx.length; i++) {
            for (int j = 0; j < gx[0].length; j++) {
                gx[i][j] = (int) Math.pow(gx[i][j],2);
                gy[i][j] = (int) Math.pow(gy[i][j],2);
                g45[i][j] = (int) Math.pow(g45[i][j],2);
                g135[i][j] = (int) Math.pow(g135[i][j],2);
            }
        }

        // Aplico filtro de gauss 7x7
        Mask mask = new Mask(maskSize);
        mask.setGaussMaskRevised(sigma);
        gx = this.applyRawConvolutionReloaded(gx,mask,gx.length,gx[0].length);
        gy = this.applyRawConvolutionReloaded(gy,mask,gy.length,gy[0].length);
        g45 = this.applyRawConvolutionReloaded(g45,mask,g45.length,g45[0].length);
        g135 = this.applyRawConvolutionReloaded(g135,mask,g135.length,g135[0].length);

        //Paso 3
        // Multiplico pixel a pixel
        int gxy[][] = new int[bimg.getWidth()][bimg.getHeight()];
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                gxy[i][j] = gx[i][j] * gy[i][j] * g45[i][j] * g135[i][j];
                //gxy[i][j] = gx[i][j] * gy[i][j];
            }
        }

        // Aplico filtro de gauss al resultado de la multiplicacion
        gxy = this.applyRawConvolutionReloaded(gxy,mask,gxy.length,gxy[0].length);

        //Paso 4
        // Aplico formula
        for (int i = 0; i < resultMatrix.length; i++) {
            for (int j = 0; j < resultMatrix[0].length; j++) {
                //resultMatrix[i][j] = (int) Math.round((gx[i][j] * gy[i][j] - Math.pow(gxy[i][j],2)) - (0.04 * Math.pow(gx[i][j] + gy[i][j],2)));
                resultMatrix[i][j] = (int) Math.round((gx[i][j] * gy[i][j] * g45[i][j] * g135[i][j] - Math.pow(gxy[i][j],2)) - (0.04 * Math.pow(gx[i][j] + gy[i][j] + g45[i][j] + g135[i][j],2)));
            }
        }

        // Busco el maximo
        int max = 0;
        for (int i = 0; i < resultMatrix.length; i++) {
            for (int j = 0; j < resultMatrix[0].length; j++) {
                if ( resultMatrix[i][j] > max){
                    max = resultMatrix[i][j];
                }
            }
        }

        //Recorro el resultado y pinto las esquinas
        BufferedImage result = imageUtilities.copyImageIntoAnother(bimg,13);
        for (int i = 0; i < bimg.getWidth(); i++) {
            for (int j = 0; j < bimg.getHeight(); j++) {
                if (resultMatrix[i][j] > percent * max){
                    result.setRGB(i,j,ColorUtilities.createRGB(0,255,0));
                }
            }
        }
        return result;
    }
}
