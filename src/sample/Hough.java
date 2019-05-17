package sample;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Hough {

    private int [][] cumulativeMatrix;
    private double [] angleArray;
    private double [] radiusArray;

    //Para crear el objeto debo indicar las dimensiones de la matriz acumuladora y rango de parametros
    public Hough(int angleLenght, int radiusLenght){
        this.cumulativeMatrix = new int[angleLenght][radiusLenght];
        this.angleArray = new double[angleLenght];
        this.radiusArray = new double[radiusLenght];
        if (cumulativeMatrix!=null){
            for (int i = 0; i < cumulativeMatrix.length; i++) {
                for (int j = 0; j < cumulativeMatrix[0].length; j++) {
                    cumulativeMatrix[i][j] = 0;
                }
            }
        }
    }

    private void discretizeParameters(int[][] imageMatrix){
        //calculo el maximo radio con pitagoras, puede ser a lo sumo el valor de la diagonal de la imagen
        double maxRadius = Math.sqrt(Math.pow(imageMatrix.length,2) + Math.pow(imageMatrix[0].length,2));
        if (angleArray !=null){
            for (int i = 0; i < angleArray.length; i++) {
                //La discretizacion es para el rango total dividido la cantidad de posiciones, este va de -90 a 90
                angleArray[i] = (i * (180 / angleArray.length)) - 90;
            }
        }
        if (radiusArray !=null){
            for (int i = 0; i < radiusArray.length; i++) {
                //La discretizacion es para el rango total dividido la cantidad de posiciones, este va de 0 a diagonal de la imagen
                radiusArray[i] = i * (maxRadius/ radiusArray.length);
            }
        }
    }

    public BufferedImage findLines(BufferedImage bimg){
        Image image = new Image(bimg);
        image.convertToGreyDataMatrix();
        int[][] greyDataMatrix = image.getGreyDataMatrix();
        this.discretizeParameters(greyDataMatrix);
        //Recorro la matriz y donde hay un blanco inicio la votacion
        for (int i = 0; i < greyDataMatrix.length; i++) {
            for (int j = 0; j < greyDataMatrix[0].length; j++) {
                if(greyDataMatrix[i][j]==255){
                    this.votePixel(i,j);
                }
            }
        }
        //Busco los mas votados
        int max = getMaxVotedParameters();
        //Recorro la matriz acumulada para buscar los parametros dentro del 80% del maximo
        for (int i = 0; i < cumulativeMatrix.length; i++) {
            for (int j = 0; j < cumulativeMatrix[0].length; j++) {
                if (cumulativeMatrix[i][j] >= max * 0.9){
                    //Dibujo las lineas si supera el 80% del max
                    System.out.println("Dibujando linea con angulo: " +  angleArray[i] + " radio: " + radiusArray[j] );
                    bimg = drawLine(bimg, angleArray[i], radiusArray[j]);
                }
            }
        }
        return bimg;
    }

    private void votePixel(int x, int y){
        //recorro los arrays de angulo y radio en el mismo sentido que recorreria la matriz acumuladora
        for (int i = 0; i < angleArray.length; i++) {
            for (int j = 0; j < radiusArray.length; j++) {
                double angle = angleArray[i] * Math.PI / 180;
                double radius = Math.round(radiusArray[j]);
                double line = Math.round(x * Math.cos(angle)) + (y * Math.sin(angle));
                //Si la posicion del pixel pertenece a una recta dada por ambos parametros entonces incremento la matriz acumulada
                if ( line == radius){
                    cumulativeMatrix[i][j]++;
                }
            }
        }
    }

    public int getMaxVotedParameters(){
        int max = 0;
        //Recorro 1 vez para calcular el maximo
        for (int i = 0; i < cumulativeMatrix.length; i++) {
            for (int j = 0; j < cumulativeMatrix[0].length; j++) {
                if (max < cumulativeMatrix[i][j]){
                    max = cumulativeMatrix[i][j];
                }
            }
        }

        return max;
    }

    public BufferedImage drawLine(BufferedImage bimg, double angle, double radius){
        System.out.println(angle);
        angle = angle * Math.PI / 180;
        radius = Math.round(radius);
        Image image = new Image(bimg);
        image.convertToGreyDataMatrix();
        int[][] greyDataMatrix = image.getGreyDataMatrix();
        int [] XYPositions = new int [4];
        int counter = 0;
        for (int i = 0; i < greyDataMatrix.length; i++) {
            for (int j = 0; j < greyDataMatrix[0].length; j++) {

                    if(greyDataMatrix[i][j]==255){
                        //System.out.println("Punto blanco en i: " + i + " j: " + j);
                        double  line = Math.round(i * Math.cos(angle)) + (j * Math.sin(angle));
                        //System.out.println("line: " + line + " radius: " + radius);
                        if ( line == radius){
                            //System.out.println("i: " + i + " j: " + j + " angle: " + angle + " radius: " + radius);
                            if (counter <= 2){
                                XYPositions[counter]=i;
                                XYPositions[counter+1]=j;
                            counter += 2;
                        }
                    }

                }
            }
        }

        int x1 = XYPositions[0];
        int y1 = XYPositions[1];
        int x2 = XYPositions[2];
        int y2 = XYPositions[3];

        double m = (double) (y2-y1)/(x2-x1);
        double b =  ((double)y1 -(m*x1));

        //System.out.println("x1: " + x1 + " y1: " + y1 + " x2: " + endX + " y2: " + endY);
        Graphics2D g2d = bimg.createGraphics();
        g2d.setColor(Color.RED );
        BasicStroke bs = new BasicStroke(1);
        g2d.setStroke(bs);

        //Para dibujar una linea infinita voy recorriendo la formula de la recta y pintando de a 1 punto
        if (x1!=x2){
        for (int i = 0; i < bimg.getWidth()*bimg.getHeight(); i++) {
            if(i < bimg.getWidth() && i < bimg.getHeight()){
                    x1 = i;
                    y1 = (int) Math.round(m*x1+b);
                    g2d.drawLine(x1,y1,x1,y1);
                }
            }
        }
        // Si x1 y x2 son iguales quiere decir que estamos en una recta vertical incremento y dejando x fijo
        else{
            for (int i = 0; i < bimg.getWidth()*bimg.getHeight(); i++) {
                if(i < bimg.getWidth() && i < bimg.getHeight()){
                    y1 = i;
                    g2d.drawLine(x1,y1,x1,y1);
                }
            }
        }
        return bimg;
    }

}
